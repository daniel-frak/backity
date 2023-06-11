package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.backup.fakes.FakeUnixFileManager;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupUrlEmptyException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.files.domain.backup.model.*;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.files.domain.game.GameId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileBackupServiceTest {

    private FileBackupService fileBackupService;

    @Mock
    private FilePathProvider filePathProvider;

    @Mock
    private GameFileDetailsRepository gameFileDetailsRepository;

    @Mock
    private SourceFileBackupService sourceFileBackupService;

    private FakeUnixFileManager fileManager;

    @BeforeEach
    void setUp() {
        when(sourceFileBackupService.getSource())
                .thenReturn("someSourceId1");
        fileManager = new FakeUnixFileManager(5000);
        fileBackupService = new FileBackupService(filePathProvider, gameFileDetailsRepository, fileManager,
                singletonList(sourceFileBackupService));
    }

    @Test
    void shouldDownloadGameFile() throws IOException {
        String source = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = TestGameFileDetails.GAME_FILE_DETAILS_1.get();
        var tempFilePath = "someFileDir/someFile";
        var expectedFilePath = "finalFilePath";

        List<FileBackupStatus> savedFileBackupStatuses = new ArrayList<>();
        List<String> savedFilePaths = new ArrayList<>();
        when(gameFileDetailsRepository.save(any()))
                .then(a -> {
                    GameFileDetails argument = a.getArgument(0, GameFileDetails.class);
                    savedFileBackupStatuses.add(argument.getBackupDetails().getStatus());
                    savedFilePaths.add(argument.getBackupDetails().getFilePath());
                    return argument;
                });

        when(filePathProvider.createTemporaryFilePath(source,
                gameFileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);
        when(sourceFileBackupService.backUpGameFile(gameFileDetails, tempFilePath))
                .thenReturn(expectedFilePath);

        fileBackupService.backUpGameFile(gameFileDetails);

        assertTrue(fileManager.freeSpaceWasCheckedFor(tempFilePath));

        assertEquals(List.of(FileBackupStatus.IN_PROGRESS, FileBackupStatus.IN_PROGRESS, FileBackupStatus.SUCCESS),
                savedFileBackupStatuses);
        assertEquals(Arrays.asList(null, tempFilePath, expectedFilePath), savedFilePaths);
    }

    @Test
    void shouldTryToRemoveTempFileAndRethrowWrappedOnIOException() throws IOException {
        String source = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = TestGameFileDetails.GAME_FILE_DETAILS_1.get();
        var tempFilePath = "someFileDir/someFile";

        when(filePathProvider.createTemporaryFilePath(source,
                gameFileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);
        IOException coreException = new IOException("someMessage");
        when(sourceFileBackupService.backUpGameFile(gameFileDetails, tempFilePath))
                .thenThrow(coreException);

        var exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileDetails));
        assertEquals(coreException, exception.getCause());
        assertTrue(fileManager.fileWasDeleted(tempFilePath));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void downloadGameFileShouldThrowIfUrlIsEmpty(String url) {
        GameFileDetails gameFileDetails = createGameFileDetails("someSourceId", url);
        FileBackupFailedException exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileDetails));
        assertEquals(FileBackupUrlEmptyException.class, exception.getCause().getClass());
    }

    private GameFileDetails createGameFileDetails(String sourceId, String url) {
        return new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281")),
                new SourceFileDetails(
                        sourceId,
                        "someOriginalGameTitle1",
                        "someFileTitle1",
                        "someVersion1",
                        url,
                        "someOriginalFileName1",
                        "5 KB"
                ),
                new BackupDetails(
                        FileBackupStatus.ENQUEUED,
                        null,
                        null
                ),
                LocalDateTime.parse("2022-04-29T14:15:53"),
                LocalDateTime.parse("2023-04-29T14:15:53")
        );
    }

    @Test
    void downloadGameFileShouldThrowIfSourceDownloaderNotFound() throws IOException {
        var sourceId = "nonExistentSource1";
        var gameFileDetails = createGameFileDetails(sourceId, "someUrl");
        var tempFilePath = "someFileDir/someFile";

        lenient().when(filePathProvider.createTemporaryFilePath(eq(sourceId),
                        eq(gameFileDetails.getSourceFileDetails().originalGameTitle())))
                .thenReturn(tempFilePath);

        FileBackupFailedException exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileDetails));
        assertEquals(IllegalArgumentException.class, exception.getCause().getClass());
    }

    @Test
    void downloadGameFileShouldThrowIfIOExceptionOccurs() throws IOException {
        var sourceId = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = TestGameFileDetails.GAME_FILE_DETAILS_1.get();
        gameFileDetails.setSourceFileDetails(new SourceFileDetails(
                sourceId,
                gameFileDetails.getSourceFileDetails().originalGameTitle(),
                gameFileDetails.getSourceFileDetails().fileTitle(),
                gameFileDetails.getSourceFileDetails().version(),
                gameFileDetails.getSourceFileDetails().url(),
                gameFileDetails.getSourceFileDetails().originalFileName(),
                gameFileDetails.getSourceFileDetails().size()
        ));

        when(filePathProvider.createTemporaryFilePath(sourceId,
                gameFileDetails.getSourceFileDetails().originalGameTitle()))
                .thenThrow(new IOException());

        FileBackupFailedException exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileDetails));
        assertEquals(IOException.class, exception.getCause().getClass());
    }

    @Test
    void downloadGameFileShouldThrowIfNotEnoughFreeSpace() throws IOException {
        var sourceId = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = TestGameFileDetails.GAME_FILE_DETAILS_1.get();
        gameFileDetails.setSourceFileDetails(new SourceFileDetails(
                sourceId,
                gameFileDetails.getSourceFileDetails().originalGameTitle(),
                gameFileDetails.getSourceFileDetails().fileTitle(),
                gameFileDetails.getSourceFileDetails().version(),
                gameFileDetails.getSourceFileDetails().url(),
                gameFileDetails.getSourceFileDetails().originalFileName(),
                gameFileDetails.getSourceFileDetails().size()
        ));
        var tempFilePath = "someFileDir/someFile";

        when(filePathProvider.createTemporaryFilePath(sourceId,
                gameFileDetails.getSourceFileDetails().originalGameTitle()))
                .thenReturn(tempFilePath);

        fileManager.setAvailableSizeInBytes(0);

        FileBackupFailedException exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileDetails));
        assertEquals(NotEnoughFreeSpaceException.class, exception.getCause().getClass());
    }

    @Test
    void isReadyForShouldReturnTrueIfFileIsReadyToDownload() {
        GameFileDetails gameFileDetails = TestGameFileDetails.GAME_FILE_DETAILS_1.get();

        when(sourceFileBackupService.isReady())
                .thenReturn(true);

        assertTrue(fileBackupService.isReadyFor(gameFileDetails));
    }

    @Test
    void isReadyForShouldReturnFalseIfFileIsNotReadyToDownload() {
        GameFileDetails gameFileDetails = TestGameFileDetails.GAME_FILE_DETAILS_1.get();

        when(sourceFileBackupService.isReady())
                .thenReturn(false);

        assertFalse(fileBackupService.isReadyFor(gameFileDetails));
    }
}