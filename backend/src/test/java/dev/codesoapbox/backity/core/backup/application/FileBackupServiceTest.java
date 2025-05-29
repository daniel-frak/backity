package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgressFactory;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.domain.TestBackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.filecopy.domain.TestFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.TestFileSource;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
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

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileBackupServiceTest {

    private static final GameProviderId EXISTING_GAME_PROVIDER_ID = new GameProviderId("GOG");

    private FileBackupService fileBackupService;

    @Mock
    private UniqueFilePathResolver uniqueFilePathResolver;

    @Mock
    private FileCopyRepository fileCopyRepository;

    @Mock
    private BackupTargetRepository backupTargetRepository;

    @Mock
    private StorageSolutionRepository storageSolutionRepository;

    @Mock
    private GameProviderFileBackupService gameProviderFileBackupService;

    @Mock
    private DownloadProgressFactory downloadProgressFactory;

    @BeforeEach
    void setUp() {
        when(gameProviderFileBackupService.getGameProviderId())
                .thenReturn(EXISTING_GAME_PROVIDER_ID);
        fileBackupService = new FileBackupService(uniqueFilePathResolver, fileCopyRepository, backupTargetRepository,
                storageSolutionRepository, singletonList(gameProviderFileBackupService), downloadProgressFactory);
    }

    @Test
    void shouldDownloadFile() {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.tracked();
        PersistedChangesToFileCopy persistedChangesToFileCopy = trackPersistedChangesToFileCopy();
        BackupTarget backupTarget = mockBackupTargetExists(fileCopy);
        FakeUnixStorageSolution storageSolution = mockStorageSolutionExists(backupTarget);
        String expectedFilePath = mockFilePathCreation(backupTarget.getPathTemplate(), gameFile, storageSolution);

        fileBackupService.backUpFile(gameFile, fileCopy);

        assertThat(persistedChangesToFileCopy.savedFileCopyStatuses())
                .isEqualTo(List.of(FileCopyStatus.IN_PROGRESS, FileCopyStatus.IN_PROGRESS,
                        FileCopyStatus.STORED_INTEGRITY_UNKNOWN));
        assertThat(persistedChangesToFileCopy.savedFilePaths())
                .isEqualTo(Arrays.asList(null, // Mark 'in progress' with no file path
                        expectedFilePath, // Set file path before starting download
                        expectedFilePath // Mark 'done'
                ));
    }

    private FakeUnixStorageSolution mockStorageSolutionExists(BackupTarget backupTarget) {
        FakeUnixStorageSolution storageSolution = new FakeUnixStorageSolution(5120);
        when(storageSolutionRepository.getById(backupTarget.getStorageSolutionId()))
                .thenReturn(storageSolution);
        return storageSolution;
    }

    private BackupTarget mockBackupTargetExists(FileCopy fileCopy) {
        BackupTarget backupTarget = TestBackupTarget.localFolder();
        when(backupTargetRepository.getById(fileCopy.getNaturalId().backupTargetId()))
                .thenReturn(backupTarget);
        return backupTarget;
    }

    private String mockFilePathCreation(String pathTemplate, GameFile gameFile, StorageSolution storageSolution) {
        var filePath = "someFileDir/someFile";
        when(uniqueFilePathResolver.resolve(pathTemplate, gameFile.getFileSource(), storageSolution))
                .thenReturn(filePath);

        return filePath;
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

    @Test
    void shouldTryToRemoveFileAndRethrowWrappedGivenIOException() throws IOException {
        GameFile gameFile = TestGameFile.gog();
        FileCopy fileCopy = TestFileCopy.tracked();
        BackupTarget backupTarget = mockBackupTargetExists(fileCopy);
        FakeUnixStorageSolution storageSolution = mockStorageSolutionExists(backupTarget);
        String filePath = mockFilePathCreation(backupTarget.getPathTemplate(), gameFile, storageSolution);
        IOException coreException = mockGameProviderServiceThrowsExceptionDuringBackup();

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile, fileCopy))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertThat(fileCopy)
                .satisfies(it -> assertSoftly(softly -> {
                    softly.assertThat(it.getStatus()).isEqualTo(FileCopyStatus.FAILED);
                    softly.assertThat(it.getFailedReason()).isEqualTo(coreException.getMessage());
                }));
        verify(fileCopyRepository, times(4)).save(fileCopy);
        assertThat(storageSolution.fileDeleteWasAttempted(filePath)).isTrue();
        assertThat(fileCopy.getFilePath()).isNull();
    }

    private IOException mockGameProviderServiceThrowsExceptionDuringBackup() throws IOException {
        var coreException = new IOException("someMessage");
        doThrow(coreException)
                .when(gameProviderFileBackupService).backUpFile(any(), any(), any(), any());

        return coreException;
    }

    @Test
    void downloadFileShouldThrowIfGameProviderFileBackupServiceNotFound() {
        var nonExistentGameProviderId = new GameProviderId("nonExistentGameProviderId1");
        GameFile gameFile = TestGameFile.gogBuilder()
                .fileSource(TestFileSource.minimalGogBuilder()
                        .gameProviderId(nonExistentGameProviderId)
                        .build())
                .build();
        FileCopy fileCopy = TestFileCopy.tracked();
        BackupTarget backupTarget = mockBackupTargetExists(fileCopy);
        StorageSolution storageSolution = mockStorageSolutionExists(backupTarget);
        mockFilePathCreation(backupTarget.getPathTemplate(), gameFile, storageSolution);

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile, fileCopy))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File backup service for gameProviderId not found: nonExistentGameProviderId1");
    }

    @Test
    void downloadFileShouldThrowIfIOExceptionOccurs() throws IOException {
        GameProviderId gameProviderId = gameProviderFileBackupService.getGameProviderId();
        GameFile gameFile = TestGameFile.gogBuilder()
                .fileSource(TestFileSource.minimalGogBuilder()
                        .gameProviderId(gameProviderId)
                        .build())
                .build();
        FileCopy fileCopy = TestFileCopy.tracked();
        BackupTarget backupTarget = mockBackupTargetExists(fileCopy);
        mockStorageSolutionExists(backupTarget);
        mockFileBackupThrowsIOException();

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile, fileCopy))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IOException.class);
        assertThat(fileCopy)
                .satisfies(it -> assertSoftly(softly -> {
                    softly.assertThat(it.getStatus()).isEqualTo(FileCopyStatus.FAILED);
                    softly.assertThat(it.getFailedReason()).isEqualTo("Unknown error");
                }));
    }

    private void mockFileBackupThrowsIOException() throws IOException {
        doThrow(new IOException())
                .when(gameProviderFileBackupService).backUpFile(any(), any(), any(), any());
    }

    @Test
    void isReadyForShouldReturnTrueIfFileIsReadyToDownload() {
        GameFile gameFile = TestGameFile.gog();
        when(gameProviderFileBackupService.isReady())
                .thenReturn(true);

        assertThat(fileBackupService.isReadyFor(gameFile)).isTrue();
    }

    @Test
    void isReadyForShouldReturnFalseIfFileIsNotReadyToDownload() {
        GameFile gameFile = TestGameFile.gog();
        when(gameProviderFileBackupService.isReady())
                .thenReturn(false);

        assertThat(fileBackupService.isReadyFor(gameFile)).isFalse();
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