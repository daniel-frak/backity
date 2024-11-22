package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupUrlEmptyException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.NotEnoughFreeSpaceException;
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

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileBackupServiceTest {

    private FileBackupService fileBackupService;

    @Mock
    private FilePathProvider filePathProvider;

    @Mock
    private GameFileRepository gameFileRepository;

    @Mock
    private GameProviderFileBackupService gameProviderFileBackupService;

    private FakeUnixFileManager fileManager;

    @BeforeEach
    void setUp() {
        when(gameProviderFileBackupService.getGameProviderId())
                .thenReturn(new GameProviderId("someGameProviderId"));
        fileManager = new FakeUnixFileManager(5000);
        fileBackupService = new FileBackupService(filePathProvider, gameFileRepository, fileManager,
                singletonList(gameProviderFileBackupService));
    }

    @Test
    void shouldDownloadFile() throws IOException {
        GameProviderId gameProviderId = gameProviderFileBackupService.getGameProviderId();
        GameFile gameFile = discoveredGameFile().build();
        var tempFilePath = "someFileDir/someFile";
        var expectedFilePath = "finalFilePath";
        List<FileBackupStatus> savedFileBackupStatuses = new ArrayList<>();
        List<String> savedFilePaths = new ArrayList<>();
        when(gameFileRepository.save(any()))
                .then(a -> {
                    GameFile argument = a.getArgument(0, GameFile.class);
                    savedFileBackupStatuses.add(argument.getFileBackup().getStatus());
                    savedFilePaths.add(argument.getFileBackup().getFilePath());
                    return argument;
                });
        when(filePathProvider.createTemporaryFilePath(gameProviderId,
                gameFile.getGameProviderFile().originalGameTitle()))
                .thenReturn(tempFilePath);
        when(gameProviderFileBackupService.backUpFile(gameFile, tempFilePath))
                .thenReturn(expectedFilePath);

        fileBackupService.backUpFile(gameFile);

        assertThat(fileManager.freeSpaceWasCheckedFor(tempFilePath)).isTrue();
        assertThat(savedFileBackupStatuses)
                .isEqualTo(List.of(FileBackupStatus.IN_PROGRESS, FileBackupStatus.IN_PROGRESS,
                        FileBackupStatus.SUCCESS));
        assertThat(savedFilePaths)
                .isEqualTo(Arrays.asList(null, tempFilePath, expectedFilePath));
    }

    @Test
    void shouldTryToRemoveTempFileAndRethrowWrappedOnIOExceptionGivenFilePathIsTempFilePath() throws IOException {
        GameProviderId gameProviderId = gameProviderFileBackupService.getGameProviderId();
        GameFile gameFile = discoveredGameFile().build();
        var tempFilePath = "someFileDir/someFile";
        var coreException = new IOException("someMessage");
        when(filePathProvider.createTemporaryFilePath(gameProviderId,
                gameFile.getGameProviderFile().originalGameTitle()))
                .thenReturn(tempFilePath);
        when(gameProviderFileBackupService.backUpFile(gameFile, tempFilePath))
                .thenThrow(coreException);

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isEqualTo(coreException);
        assertThat(gameFile.getFileBackup())
                .satisfies(fileBackup -> assertSoftly(softly -> {
                    softly.assertThat(fileBackup.getStatus()).isEqualTo(FileBackupStatus.FAILED);
                    softly.assertThat(fileBackup.getFailedReason()).isEqualTo(coreException.getMessage());
                }));
        verify(gameFileRepository, times(4)).save(gameFile);
        assertThat(fileManager.fileDeleteWasAttempted(tempFilePath)).isTrue();
        assertThat(gameFile.getFileBackup().getFilePath()).isNull();
    }

    @Test
    void shouldTryToRemoveTempFileAndRethrowWrappedOnIOExceptionGivenFilePathIsNotTempFilePath() throws IOException {
        GameProviderId gameProviderId = gameProviderFileBackupService.getGameProviderId();
        GameFile gameFile = discoveredGameFile().build();
        var tempFilePath = "someFileDir/someFile";
        var nonTempFilePath = "nonTempFilePath";
        var coreException = new IOException("someMessage");
        when(filePathProvider.createTemporaryFilePath(gameProviderId,
                gameFile.getGameProviderFile().originalGameTitle()))
                .thenReturn(tempFilePath);
        when(gameProviderFileBackupService.backUpFile(gameFile, tempFilePath))
                .thenAnswer(inv -> {
                    gameFile.getFileBackup().setFilePath(nonTempFilePath);
                    throw coreException;
                });

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isEqualTo(coreException);
        assertThat(gameFile.getFileBackup())
                .satisfies(fileBackup -> assertSoftly(softly -> {
                    softly.assertThat(fileBackup.getStatus()).isEqualTo(FileBackupStatus.FAILED);
                    softly.assertThat(fileBackup.getFailedReason()).isEqualTo(coreException.getMessage());
                }));
        verify(gameFileRepository, times(3)).save(gameFile);
        assertThat(gameFile.getFileBackup().getFilePath()).isEqualTo(nonTempFilePath);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void downloadFileShouldThrowIfUrlIsEmpty(String url) {
        GameFile gameFile = discoveredGameFile()
                .url(url)
                .build();

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(FileBackupUrlEmptyException.class);
    }

    @Test
    void downloadFileShouldThrowIfGameProviderIdDownloaderNotFound() throws IOException {
        var gameProviderId = new GameProviderId("nonExistentGameProviderId1");
        GameFile gameFile = discoveredGameFile()
                .gameProviderId(gameProviderId)
                .build();
        var tempFilePath = "someFileDir/someFile";
        when(filePathProvider.createTemporaryFilePath(gameProviderId,
                gameFile.getGameProviderFile().originalGameTitle()))
                .thenReturn(tempFilePath);

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void downloadFileShouldThrowIfIOExceptionOccurs() throws IOException {
        GameProviderId gameProviderId = gameProviderFileBackupService.getGameProviderId();
        GameFile gameFile = discoveredGameFile()
                .gameProviderId(gameProviderId)
                .build();

        when(filePathProvider.createTemporaryFilePath(gameProviderId,
                gameFile.getGameProviderFile().originalGameTitle()))
                .thenThrow(new IOException());

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IOException.class);
    }

    @Test
    void downloadFileShouldThrowIfNotEnoughFreeSpace() throws IOException {
        GameProviderId gameProviderId = gameProviderFileBackupService.getGameProviderId();
        GameFile gameFile = discoveredGameFile()
                .gameProviderId(gameProviderId)
                .build();
        var tempFilePath = "someFileDir/someFile";
        fileManager.setAvailableSizeInBytes(0);
        when(filePathProvider.createTemporaryFilePath(gameProviderId,
                gameFile.getGameProviderFile().originalGameTitle()))
                .thenReturn(tempFilePath);

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(NotEnoughFreeSpaceException.class);
    }

    @Test
    void isReadyForShouldReturnTrueIfFileIsReadyToDownload() {
        GameFile gameFile = discoveredGameFile().build();
        when(gameProviderFileBackupService.isReady())
                .thenReturn(true);

        assertThat(fileBackupService.isReadyFor(gameFile)).isTrue();
    }

    @Test
    void isReadyForShouldReturnFalseIfFileIsNotReadyToDownload() {
        GameFile gameFile = discoveredGameFile().build();
        when(gameProviderFileBackupService.isReady())
                .thenReturn(false);

        assertThat(fileBackupService.isReadyFor(gameFile)).isFalse();
    }
}