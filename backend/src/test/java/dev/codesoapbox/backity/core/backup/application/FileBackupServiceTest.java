package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
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
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.tracked();
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        StorageSolution storageSolution = mock(StorageSolution.class);
        mockGameProviderIsNotConnected(gameFile);

        fileBackupService.backUpFile(fileCopy, gameFile, backupTarget, storageSolution);

        verifyNoMoreInteractions(uniqueFilePathResolver, fileCopyRepository, fileCopyReplicator);
    }

    private void mockGameProviderIsNotConnected(GameFile gameFile) {
        when(fileCopyReplicator.gameProviderIsConnected(gameFile))
                .thenReturn(false);
    }

    @Test
    void shouldBackupFile() throws IOException {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.enqueued();
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        StorageSolution storageSolution = mock(StorageSolution.class);
        mockGameProviderIsConnected(gameFile);
        PersistedChangesToFileCopy persistedChangesToFileCopy = trackPersistedChangesToFileCopy();
        String expectedFilePath = mockFilePathCreation(backupTarget.getPathTemplate(), gameFile, storageSolution);

        fileBackupService.backUpFile(fileCopy, gameFile, backupTarget, storageSolution);

        assertStatusChangesWerePersisted(persistedChangesToFileCopy);
        assertFilePathWasPersisted(persistedChangesToFileCopy, expectedFilePath);
        verify(fileCopyReplicator).replicateFile(storageSolution, gameFile, fileCopy);
    }

    private void assertStatusChangesWerePersisted(PersistedChangesToFileCopy persistedChangesToFileCopy) {
        assertThat(persistedChangesToFileCopy.savedFileCopyStatuses())
                .isEqualTo(List.of(FileCopyStatus.IN_PROGRESS, FileCopyStatus.STORED_INTEGRITY_UNKNOWN));
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
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.enqueued();
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        var storageSolution = new FakeUnixStorageSolution(5120);
        mockGameProviderIsConnected(gameFile);
        String filePath = mockFilePathCreation(backupTarget.getPathTemplate(), gameFile, storageSolution);
        IOException coreException = mockFileCopyReplicatorThrowsExceptionDuringBackup();

        assertThatThrownBy(() ->
                fileBackupService.backUpFile(fileCopy, gameFile, backupTarget, storageSolution))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertIsFailedWithReason(fileCopy, coreException);
        verify(fileCopyRepository, times(2)).save(fileCopy);
        assertThat(storageSolution.fileDeleteWasAttempted(filePath)).isTrue();
        assertThat(fileCopy.getFilePath()).isNull();
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
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.enqueued();
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        StorageSolution storageSolution = mock(StorageSolution.class);
        mockGameProviderIsConnected(gameFile);
        mockFilePathCreation(backupTarget.getPathTemplate(), gameFile, storageSolution);
        IOException coreException = mockFileCopyReplicatorThrowsExceptionDuringBackup();
        mockThrowsExceptionDuringFileDelete(storageSolution);

        assertThatThrownBy(() ->
                fileBackupService.backUpFile(fileCopy, gameFile, backupTarget, storageSolution))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertIsFailedWithReason(fileCopy, coreException);
        verify(fileCopyRepository, times(2)).save(fileCopy);
        assertThat(fileCopy.getFilePath()).isNotNull();
    }

    private void mockThrowsExceptionDuringFileDelete(StorageSolution storageSolution) {
        doThrow(new RuntimeException("Test exception"))
                .when(storageSolution).deleteIfExists(any());
    }

    @Test
    void shouldNotTryToDeleteFileGivenMarkingAsFailedButPathIsNull() {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.tracked();
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        StorageSolution storageSolution = mock(StorageSolution.class);
        mockGameProviderIsConnected(gameFile);
        RuntimeException coreException = mockPathResolverThrows();

        assertThatThrownBy(() ->
                fileBackupService.backUpFile(fileCopy, gameFile, backupTarget, storageSolution))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertIsFailedWithReason(fileCopy, coreException);
        verify(fileCopyRepository, times(1)).save(fileCopy);
        assertThat(fileCopy.getFilePath()).isNull();
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
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.tracked();
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        StorageSolution storageSolution = mock(StorageSolution.class);
        mockGameProviderIsConnected(gameFile);
        RuntimeException coreException = mockPathResolverThrowsWithNullMessage();

        assertThatThrownBy(() ->
                fileBackupService.backUpFile(fileCopy, gameFile, backupTarget, storageSolution))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertThat(fileCopy.getFailedReason()).isEqualTo("Unknown error");
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