package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.domain.BackupProgress;
import dev.codesoapbox.backity.core.backup.domain.BackupProgressFactory;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.gamefile.domain.TestGameFile;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameProviderFileUrlEmptyException;
import dev.codesoapbox.backity.core.backup.application.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.filemanagement.domain.FakeUnixFileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FilePathProvider;
import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    private static final GameProviderId EXISTING_GAME_PROVIDER_ID = new GameProviderId("someGameProviderId");

    private FileBackupService fileBackupService;

    @Mock
    private FilePathProvider filePathProvider;

    @Mock
    private GameFileRepository gameFileRepository;

    @Mock
    private GameProviderFileBackupService gameProviderFileBackupService;

    @Mock
    private BackupProgressFactory backupProgressFactory;

    private FakeUnixFileManager fileManager;

    @BeforeEach
    void setUp() {
        when(gameProviderFileBackupService.getGameProviderId())
                .thenReturn(EXISTING_GAME_PROVIDER_ID);
        fileManager = new FakeUnixFileManager(5000);
        fileBackupService = new FileBackupService(filePathProvider, gameFileRepository, fileManager,
                singletonList(gameProviderFileBackupService), backupProgressFactory);
    }

    @Test
    void shouldDownloadFile() throws IOException {
        GameFile gameFile = TestGameFile.discovered();
        GameFilePersistedChanges gameFilePersistedChanges = trackPersistedGameFileChanges();
        String tempFilePath = mockTempFilePathCreation(gameFile, EXISTING_GAME_PROVIDER_ID);
        BackupProgress backupProgress = mockBackupProgressCreation();
        String expectedFilePath = mockGameProviderServiceSuccessfullyBackupsUp(
                gameFile, tempFilePath, backupProgress);

        fileBackupService.backUpFile(gameFile);

        assertThat(fileManager.freeSpaceWasCheckedFor(tempFilePath)).isTrue();
        assertThat(gameFilePersistedChanges.savedFileBackupStatuses())
                .isEqualTo(List.of(FileBackupStatus.IN_PROGRESS, FileBackupStatus.IN_PROGRESS,
                        FileBackupStatus.SUCCESS));
        assertThat(gameFilePersistedChanges.savedFilePaths())
                .isEqualTo(Arrays.asList(null, tempFilePath, expectedFilePath));
    }

    private String mockGameProviderServiceSuccessfullyBackupsUp(
            GameFile gameFile, String tempFilePath, BackupProgress backupProgress) throws IOException {
        var expectedFilePath = "finalFilePath";
        when(gameProviderFileBackupService.backUpFile(gameFile, tempFilePath, backupProgress))
                .thenReturn(expectedFilePath);

        return expectedFilePath;
    }

    private String mockTempFilePathCreation(GameFile gameFile, GameProviderId gameProviderId) throws IOException {
        var tempFilePath = "someFileDir/someFile";
        when(filePathProvider.createTemporaryFilePath(gameProviderId,
                gameFile.getGameProviderFile().originalGameTitle()))
                .thenReturn(tempFilePath);

        return tempFilePath;
    }

    private GameFilePersistedChanges trackPersistedGameFileChanges() {
        var gameFilePersistedChanges = new GameFilePersistedChanges();
        when(gameFileRepository.save(any()))
                .then(a -> {
                    GameFile argument = a.getArgument(0, GameFile.class);
                    gameFilePersistedChanges.addFor(argument);

                    return argument;
                });

        return gameFilePersistedChanges;
    }

    private BackupProgress mockBackupProgressCreation() {
        BackupProgress backupProgress = mock(BackupProgress.class);
        when(backupProgressFactory.create())
                .thenReturn(backupProgress);

        return backupProgress;
    }

    @Test
    void shouldTryToRemoveTempFileAndRethrowWrappedOnIOExceptionGivenFilePathIsTempFilePath() throws IOException {
        GameFile gameFile = TestGameFile.discovered();
        String tempFilePath = mockTempFilePathCreation(gameFile, EXISTING_GAME_PROVIDER_ID);
        BackupProgress backupProgress = mockBackupProgressCreation();
        IOException coreException = mockGameProviderServiceSetsFilePathAsTempThenThrowsExceptionDuringBackup(
                gameFile, tempFilePath, backupProgress);

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertThat(gameFile.getFileBackup())
                .satisfies(fileBackup -> assertSoftly(softly -> {
                    softly.assertThat(fileBackup.getStatus()).isEqualTo(FileBackupStatus.FAILED);
                    softly.assertThat(fileBackup.getFailedReason()).isEqualTo(coreException.getMessage());
                }));
        verify(gameFileRepository, times(4)).save(gameFile);
        assertThat(fileManager.fileDeleteWasAttempted(tempFilePath)).isTrue();
        assertThat(gameFile.getFileBackup().getFilePath()).isNull();
    }

    private IOException mockGameProviderServiceSetsFilePathAsTempThenThrowsExceptionDuringBackup(
            GameFile gameFile, String tempFilePath, BackupProgress backupProgress) throws IOException {
        var coreException = new IOException("someMessage");
        when(gameProviderFileBackupService.backUpFile(gameFile, tempFilePath, backupProgress))
                .thenAnswer(inv -> {
                    gameFile.getFileBackup().setFilePath(tempFilePath);
                    throw coreException;
                })
                .thenThrow(coreException);

        return coreException;
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void downloadFileShouldThrowIfUrlIsEmpty(String url) {
        GameFile gameFile = TestGameFile.discoveredBuilder()
                .url(url)
                .build();

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(GameProviderFileUrlEmptyException.class);
    }

    @Test
    void downloadFileShouldThrowIfGameProviderFileBackupServiceNotFound() throws IOException {
        var nonExistentGameProviderId = new GameProviderId("nonExistentGameProviderId1");
        GameFile gameFile = TestGameFile.discoveredBuilder()
                .gameProviderId(nonExistentGameProviderId)
                .build();
        mockTempFilePathCreation(gameFile, nonExistentGameProviderId);

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File backup service for gameProviderId not found: nonExistentGameProviderId1");
    }

    @Test
    void downloadFileShouldThrowIfIOExceptionOccurs() throws IOException {
        GameProviderId gameProviderId = gameProviderFileBackupService.getGameProviderId();
        GameFile gameFile = TestGameFile.discoveredBuilder()
                .gameProviderId(gameProviderId)
                .build();
        mockTempFilePathCreationThrowsIoException(gameProviderId, gameFile);

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IOException.class);
    }

    private void mockTempFilePathCreationThrowsIoException(GameProviderId gameProviderId, GameFile gameFile)
            throws IOException {
        when(filePathProvider.createTemporaryFilePath(gameProviderId,
                gameFile.getGameProviderFile().originalGameTitle()))
                .thenThrow(new IOException());
    }

    @Test
    void downloadFileShouldThrowIfNotEnoughFreeSpace() throws IOException {
        GameProviderId gameProviderId = gameProviderFileBackupService.getGameProviderId();
        GameFile gameFile = TestGameFile.discoveredBuilder()
                .gameProviderId(gameProviderId)
                .build();
        mockTempFilePathCreation(gameFile, EXISTING_GAME_PROVIDER_ID);
        fileManager.setAvailableSizeInBytes(0);

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(NotEnoughFreeSpaceException.class);
    }

    @Test
    void isReadyForShouldReturnTrueIfFileIsReadyToDownload() {
        GameFile gameFile = TestGameFile.discovered();
        when(gameProviderFileBackupService.isReady())
                .thenReturn(true);

        assertThat(fileBackupService.isReadyFor(gameFile)).isTrue();
    }

    @Test
    void isReadyForShouldReturnFalseIfFileIsNotReadyToDownload() {
        GameFile gameFile = TestGameFile.discovered();
        when(gameProviderFileBackupService.isReady())
                .thenReturn(false);

        assertThat(fileBackupService.isReadyFor(gameFile)).isFalse();
    }

    private record GameFilePersistedChanges(
            List<FileBackupStatus> savedFileBackupStatuses,
            List<String> savedFilePaths
    ) {

        private GameFilePersistedChanges() {
            this(new ArrayList<>(), new ArrayList<>());
        }

        public void addFor(GameFile gameFile) {
            savedFileBackupStatuses.add(gameFile.getFileBackup().getStatus());
            savedFilePaths.add(gameFile.getFileBackup().getFilePath());
        }
    }
}