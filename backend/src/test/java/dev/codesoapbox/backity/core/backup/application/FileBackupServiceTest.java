package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadWasCanceledException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileBackupServiceTest {

    private FileBackupService fileBackupService;

    @Mock
    private UniqueFilePathResolver uniqueFilePathResolver;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private FileCopyReplicator fileCopyReplicator;

    @BeforeEach
    void setUp() {
        fileBackupService = new FileBackupService(uniqueFilePathResolver, fileCopyRepository, fileCopyReplicator);
    }

    @Test
    void shouldDoNothingGivenGameProviderNotConnected() {
        FileBackupContext fileBackupContext = TestFileBackupContext.trackedLocalGog();
        mockGameProviderIsNotConnected(fileBackupContext.gameFile());

        fileBackupService.backUpFile(fileBackupContext);

        verifyNoMoreInteractions(uniqueFilePathResolver, fileCopyRepository, fileCopyReplicator);
    }

    private void mockGameProviderIsNotConnected(GameFile gameFile) {
        when(fileCopyReplicator.gameProviderIsConnected(gameFile))
                .thenReturn(false);
    }

    @Test
    void shouldBackupFile() throws IOException {
        FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGog();
        mockGameProviderIsConnected(fileBackupContext.gameFile());
        PersistedChangesToFileCopy persistedChangesToFileCopy = trackPersistedChangesToFileCopy();
        String expectedFilePath = mockFilePathCreation(
                fileBackupContext.backupTarget().getPathTemplate(), fileBackupContext.gameFile(),
                fileBackupContext.storageSolution());

        fileBackupService.backUpFile(fileBackupContext);

        assertOnlyPersistedStatusChangesWere(persistedChangesToFileCopy,
                List.of(FileCopyStatus.IN_PROGRESS, FileCopyStatus.STORED_INTEGRITY_UNKNOWN));
        assertFilePathWasPersisted(persistedChangesToFileCopy, expectedFilePath);
        verify(fileCopyReplicator).replicateFile(
                fileBackupContext.storageSolution(), fileBackupContext.gameFile(), fileBackupContext.fileCopy());
    }

    private void assertOnlyPersistedStatusChangesWere(PersistedChangesToFileCopy persistedChangesToFileCopy,
                                                      List<FileCopyStatus> statusChanges) {
        assertThat(persistedChangesToFileCopy.savedFileCopyStatuses())
                .isEqualTo(statusChanges);
    }

    private void assertFilePathWasPersisted(
            PersistedChangesToFileCopy persistedChangesToFileCopy, String expectedFilePath) {
        assertThat(persistedChangesToFileCopy.savedFilePaths())
                .isEqualTo(Arrays.asList(
                        expectedFilePath, // Mark 'in progress'
                        expectedFilePath // Mark 'stored'
                ));
    }

    private void mockGameProviderIsConnected(GameFile gameFile) {
        when(fileCopyReplicator.gameProviderIsConnected(gameFile))
                .thenReturn(true);
    }

    private PersistedChangesToFileCopy trackPersistedChangesToFileCopy() {
        var persistedChanges = new PersistedChangesToFileCopy();
        when(fileCopyRepository.save(any()))
                .then(a -> {
                    FileCopy fileCopy = a.getArgument(0, FileCopy.class);
                    persistedChanges.addFor(fileCopy);

                    return fileCopy;
                });

        return persistedChanges;
    }

    private String mockFilePathCreation(String pathTemplate, GameFile gameFile, StorageSolution storageSolution) {
        var filePath = "someFileDir/someFile";
        when(uniqueFilePathResolver.resolve(pathTemplate, gameFile.getFileSource(), storageSolution))
                .thenReturn(filePath);

        return filePath;
    }

    @Test
    void shouldMarkFileCopyAsFailedAndRemoveFileAndRethrowWrappedGivenIOException() throws IOException {
        var storageSolution = new FakeUnixStorageSolution(5120);
        FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                .storageSolution(storageSolution)
                .build();
        mockGameProviderIsConnected(fileBackupContext.gameFile());
        String filePath = mockFilePathCreation(fileBackupContext.backupTarget().getPathTemplate(),
                fileBackupContext.gameFile(), fileBackupContext.storageSolution());
        IOException coreException = mockFileCopyReplicatorThrowsExceptionDuringBackup();

        assertThatThrownBy(() ->
                fileBackupService.backUpFile(fileBackupContext))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertIsFailedWithReason(fileBackupContext.fileCopy(), coreException);
        verify(fileCopyRepository, times(2)).save(fileBackupContext.fileCopy());
        assertThat(storageSolution.fileDeleteWasAttempted(filePath)).isTrue();
    }

    private void assertIsFailedWithReason(FileCopy fileCopy, Throwable coreException) {
        assertThat(fileCopy)
                .satisfies(it -> assertSoftly(softly -> {
                    softly.assertThat(it.getStatus()).isEqualTo(FileCopyStatus.FAILED);
                    softly.assertThat(it.getFailedReason()).isEqualTo(coreException.getMessage());
                }));
    }

    private IOException mockFileCopyReplicatorThrowsExceptionDuringBackup() throws IOException {
        var coreException = new IOException("someMessage");
        doThrow(coreException)
                .when(fileCopyReplicator).replicateFile(any(), any(), any());

        return coreException;
    }

    @Test
    void shouldMarkFileCopyAsFailedAndRethrowWrappedOriginalExceptionGivenRemovingExistingFileThrowsAfterFailure()
            throws IOException {
        StorageSolution storageSolution = mock();
        FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                .storageSolution(storageSolution)
                .build();
        mockGameProviderIsConnected(fileBackupContext.gameFile());
        mockFilePathCreation(fileBackupContext.backupTarget().getPathTemplate(), fileBackupContext.gameFile(),
                fileBackupContext.storageSolution());
        IOException coreException = mockFileCopyReplicatorThrowsExceptionDuringBackup();
        mockThrowsExceptionDuringFileDelete(storageSolution);

        assertThatThrownBy(() ->
                fileBackupService.backUpFile(fileBackupContext))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertIsFailedWithReason(fileBackupContext.fileCopy(), coreException);
        verify(fileCopyRepository, times(2)).save(fileBackupContext.fileCopy());
        assertThat(fileBackupContext.fileCopy().getFilePath()).isNotNull();
    }

    private void mockThrowsExceptionDuringFileDelete(StorageSolution storageSolution) {
        doThrow(new RuntimeException("Test exception"))
                .when(storageSolution).deleteIfExists(any());
    }

    @Test
    void shouldNotTryToDeleteFileGivenMarkingAsFailedButPathIsNull() {
        StorageSolution storageSolution = mock();
        FileBackupContext fileBackupContext = TestFileBackupContext.trackedLocalGogBuilder()
                .storageSolution(storageSolution)
                .build();
        mockGameProviderIsConnected(fileBackupContext.gameFile());
        RuntimeException coreException = mockPathResolverThrows();

        assertThatThrownBy(() ->
                fileBackupService.backUpFile(fileBackupContext))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertIsFailedWithReason(fileBackupContext.fileCopy(), coreException);
        verify(fileCopyRepository, times(1)).save(fileBackupContext.fileCopy());
        assertThat(fileBackupContext.fileCopy().getFilePath()).isNull();
        verify(storageSolution, never()).deleteIfExists(any());
    }

    private RuntimeException mockPathResolverThrows() {
        var coreException = new RuntimeException("Test exception");
        when(uniqueFilePathResolver.resolve(any(), any(), any()))
                .thenThrow(coreException);
        return coreException;
    }

    @Test
    void shouldMarkFailedWithUnknownErrorMessageGivenExceptionMessageIsNull() {
        FileBackupContext fileBackupContext = TestFileBackupContext.trackedLocalGog();
        mockGameProviderIsConnected(fileBackupContext.gameFile());
        RuntimeException coreException = mockPathResolverThrowsWithNullMessage();

        assertThatThrownBy(() ->
                fileBackupService.backUpFile(fileBackupContext))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertThat(fileBackupContext.fileCopy().getFailedReason()).isEqualTo("Unknown error");
    }

    @Test
    void shouldMarkTrackedGivenFileDownloadWasCanceled() throws IOException {
        StorageSolution storageSolution = mock(StorageSolution.class);
        FileBackupContext fileBackupContext = TestFileBackupContext.enqueuedLocalGogBuilder()
                .storageSolution(storageSolution)
                .build();
        mockGameProviderIsConnected(fileBackupContext.gameFile());
        PersistedChangesToFileCopy persistedChangesToFileCopy = trackPersistedChangesToFileCopy();
        mockFilePathCreation(fileBackupContext.backupTarget().getPathTemplate(), fileBackupContext.gameFile(),
                storageSolution);
        doThrow(new FileDownloadWasCanceledException("someFilePath"))
                .when(fileCopyReplicator).replicateFile(storageSolution, fileBackupContext.gameFile(),
                        fileBackupContext.fileCopy());

        fileBackupService.backUpFile(fileBackupContext);

        verify(storageSolution).deleteIfExists(any());
        assertOnlyPersistedStatusChangesWere(persistedChangesToFileCopy,
                List.of(FileCopyStatus.IN_PROGRESS, FileCopyStatus.TRACKED));
        assertThat(fileBackupContext.fileCopy().getFilePath()).isNull();
    }

    private RuntimeException mockPathResolverThrowsWithNullMessage() {
        RuntimeException coreException = mock(RuntimeException.class);
        when(uniqueFilePathResolver.resolve(any(), any(), any()))
                .thenThrow(coreException);
        return coreException;
    }

    private record PersistedChangesToFileCopy(
            List<FileCopyStatus> savedFileCopyStatuses,
            List<String> savedFilePaths
    ) {

        private PersistedChangesToFileCopy() {
            this(new ArrayList<>(), new ArrayList<>());
        }

        public void addFor(FileCopy fileCopy) {
            savedFileCopyStatuses.add(fileCopy.getStatus());
            savedFilePaths.add(fileCopy.getFilePath());
        }
    }
}