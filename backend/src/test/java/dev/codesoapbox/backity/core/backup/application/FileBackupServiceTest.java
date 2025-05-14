package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgressFactory;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.storagesolution.domain.FakeUnixStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePathProvider;
import dev.codesoapbox.backity.core.gamefile.domain.*;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameProviderFileUrlEmptyException;
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

    private static final GameProviderId EXISTING_GAME_PROVIDER_ID = new GameProviderId("GOG");

    private FileBackupService fileBackupService;

    @Mock
    private FilePathProvider filePathProvider;

    @Mock
    private GameFileRepository gameFileRepository;

    @Mock
    private GameProviderFileBackupService gameProviderFileBackupService;

    @Mock
    private DownloadProgressFactory downloadProgressFactory;

    private FakeUnixStorageSolution storageSolution;

    @BeforeEach
    void setUp() {
        when(gameProviderFileBackupService.getGameProviderId())
                .thenReturn(EXISTING_GAME_PROVIDER_ID);
        storageSolution = new FakeUnixStorageSolution(5120);
        fileBackupService = new FileBackupService(filePathProvider, gameFileRepository, storageSolution,
                singletonList(gameProviderFileBackupService), downloadProgressFactory);
    }

    @Test
    void shouldDownloadFile() {
        GameFile gameFile = TestGameFile.discovered();
        GameFilePersistedChanges gameFilePersistedChanges = trackPersistedGameFileChanges();
        String expectedFilePath = mockFilePathCreation(gameFile, EXISTING_GAME_PROVIDER_ID);

        fileBackupService.backUpFile(gameFile);

        assertThat(gameFilePersistedChanges.savedFileBackupStatuses())
                .isEqualTo(List.of(FileBackupStatus.IN_PROGRESS, FileBackupStatus.IN_PROGRESS,
                        FileBackupStatus.SUCCESS));
        assertThat(gameFilePersistedChanges.savedFilePaths())
                .isEqualTo(Arrays.asList(null, // Mark 'in progress' with no file path
                        expectedFilePath, // Set file path before starting download
                        expectedFilePath // Mark 'done'
                ));
    }

    private String mockFilePathCreation(GameFile gameFile, GameProviderId gameProviderId) {
        var filePath = "someFileDir/someFile";
        when(filePathProvider.buildUniqueFilePath(gameProviderId,
                gameFile.getGameProviderFile().originalGameTitle(), gameFile.getGameProviderFile().originalFileName()))
                .thenReturn(filePath);

        return filePath;
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

    @Test
    void shouldTryToRemoveFileAndRethrowWrappedGivenIOException() throws IOException {
        GameFile gameFile = TestGameFile.discovered();
        String filePath = mockFilePathCreation(gameFile, EXISTING_GAME_PROVIDER_ID);
        IOException coreException = mockGameProviderServiceThrowsExceptionDuringBackup();

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .hasCause(coreException);
        assertThat(gameFile.getFileBackup())
                .satisfies(fileBackup -> assertSoftly(softly -> {
                    softly.assertThat(fileBackup.getStatus()).isEqualTo(FileBackupStatus.FAILED);
                    softly.assertThat(fileBackup.getFailedReason()).isEqualTo(coreException.getMessage());
                }));
        verify(gameFileRepository, times(4)).save(gameFile);
        assertThat(storageSolution.fileDeleteWasAttempted(filePath)).isTrue();
        assertThat(gameFile.getFileBackup().getFilePath()).isNull();
    }

    private IOException mockGameProviderServiceThrowsExceptionDuringBackup() throws IOException {
        var coreException = new IOException("someMessage");
        doThrow(coreException)
                .when(gameProviderFileBackupService).backUpFile(any(), any());

        return coreException;
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void downloadFileShouldThrowIfUrlIsEmpty(String url) {
        GameFile gameFile = TestGameFile.discoveredBuilder()
                .gameProviderFile(TestGameProviderFile.gogBuilder()
                        .url(url)
                        .build())
                .build();

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(GameProviderFileUrlEmptyException.class);
    }

    @Test
    void downloadFileShouldThrowIfGameProviderFileBackupServiceNotFound() {
        var nonExistentGameProviderId = new GameProviderId("nonExistentGameProviderId1");
        GameFile gameFile = TestGameFile.discoveredBuilder()
                .gameProviderFile(TestGameProviderFile.gogBuilder()
                        .gameProviderId(nonExistentGameProviderId)
                        .build())
                .build();
        mockFilePathCreation(gameFile, nonExistentGameProviderId);

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IllegalArgumentException.class)
                .hasMessage("File backup service for gameProviderId not found: nonExistentGameProviderId1");
    }

    @Test
    void downloadFileShouldThrowIfIOExceptionOccurs() throws IOException {
        GameProviderId gameProviderId = gameProviderFileBackupService.getGameProviderId();
        GameFile gameFile = TestGameFile.discoveredBuilder()
                .gameProviderFile(TestGameProviderFile.gogBuilder()
                        .gameProviderId(gameProviderId)
                        .build())
                .build();
        mockFileBackupThrowsIOException();

        assertThatThrownBy(() -> fileBackupService.backUpFile(gameFile))
                .isInstanceOf(FileBackupFailedException.class)
                .cause().isInstanceOf(IOException.class);
    }

    private void mockFileBackupThrowsIOException() throws IOException {
        doThrow(new IOException())
                .when(gameProviderFileBackupService).backUpFile(any(), any());
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