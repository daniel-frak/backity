package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.backup.fakes.FakeUnixFileManager;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupUrlEmptyException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
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
                .thenReturn("someSource");
        fileManager = new FakeUnixFileManager(5000);
        fileBackupService = new FileBackupService(filePathProvider, gameFileDetailsRepository, fileManager,
                singletonList(sourceFileBackupService));
    }

    @Test
    void shouldDownloadGameFile() throws IOException {
        String source = sourceFileBackupService.getSource();
        var gameTitle = "someGameTitle";

        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                source, "someUrl", "someTitle", "someOriginalFileName",
                null, gameTitle, "someGameId", "someVersion", "5 KB", null,
                null, FileBackupStatus.DISCOVERED, null);
        var tempFilePath = "someFileDir/someFile";
        var expectedFilePath = "finalFilePath";

        List<FileBackupStatus> savedFileBackupStatuses = new ArrayList<>();
        List<String> savedFilePaths = new ArrayList<>();
        when(gameFileDetailsRepository.save(any()))
                .then(a -> {
                    GameFileDetails argument = a.getArgument(0, GameFileDetails.class);
                    savedFileBackupStatuses.add(argument.getBackupStatus());
                    savedFilePaths.add(argument.getFilePath());
                    return argument;
                });

        when(filePathProvider.createTemporaryFilePath(source, gameTitle))
                .thenReturn(tempFilePath);
        when(sourceFileBackupService.backUpGameFile(gameFileVersionBackup, tempFilePath))
                .thenReturn(expectedFilePath);

        fileBackupService.backUpGameFile(gameFileVersionBackup);

        assertTrue(fileManager.freeSpaceWasCheckedFor(tempFilePath));

        assertEquals(List.of(FileBackupStatus.IN_PROGRESS, FileBackupStatus.IN_PROGRESS, FileBackupStatus.SUCCESS),
                savedFileBackupStatuses);
        assertEquals(Arrays.asList(null, tempFilePath, expectedFilePath), savedFilePaths);
    }

    @Test
    void shouldTryToRemoveTempFileAndRethrowWrappedOnIOException() throws IOException {
        String source = sourceFileBackupService.getSource();
        var gameTitle = "someGameTitle";
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                source, "someUrl", "someTitle", "someOriginalFileName",
                null, gameTitle, "someGameId", "someVersion", "5 KB", null,
                null, FileBackupStatus.DISCOVERED, null);
        var tempFilePath = "someFileDir/someFile";

        when(filePathProvider.createTemporaryFilePath(source, gameTitle))
                .thenReturn(tempFilePath);
        IOException coreException = new IOException("someMessage");
        when(sourceFileBackupService.backUpGameFile(gameFileVersionBackup, tempFilePath))
                .thenThrow(coreException);

        var exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileVersionBackup));
        assertEquals(coreException, exception.getCause());
        assertTrue(fileManager.fileWasDeleted(tempFilePath));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void downloadGameFileShouldThrowIfUrlIsEmpty(String url) {
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", url, "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "5 KB", null,
                null, FileBackupStatus.DISCOVERED, null);

        FileBackupFailedException exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileVersionBackup));
        assertEquals(FileBackupUrlEmptyException.class, exception.getCause().getClass());
    }

    @Test
    void downloadGameFileShouldThrowIfSourceDownloaderNotFound() throws IOException {
        var source = "nonExistentSource";
        var gameTitle = "someGameTitle";
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                source, "someUrl", "someTitle", "someOriginalFileName",
                null, gameTitle, "someGameId", "someVersion", "5 KB", null,
                null, FileBackupStatus.DISCOVERED, null);
        var tempFilePath = "someFileDir/someFile";

        lenient().when(filePathProvider.createTemporaryFilePath(eq(source), eq(gameTitle)))
                .thenReturn(tempFilePath);

        FileBackupFailedException exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileVersionBackup));
        assertEquals(IllegalArgumentException.class, exception.getCause().getClass());

    }

    @Test
    void downloadGameFileShouldThrowIfIOExceptionOccurs() throws IOException {
        var source = sourceFileBackupService.getSource();
        var gameTitle = "someGameTitle";
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                source, "someUrl", "someTitle", "someOriginalFileName",
                null, gameTitle, "someGameId", "someVersion", "5 KB", null,
                null, FileBackupStatus.DISCOVERED, null);

        when(filePathProvider.createTemporaryFilePath(source, gameTitle))
                .thenThrow(new IOException());

        FileBackupFailedException exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileVersionBackup));
        assertEquals(IOException.class, exception.getCause().getClass());
    }

    @Test
    void downloadGameFileShouldThrowIfNotEnoughFreeSpace() throws IOException {
        var source = sourceFileBackupService.getSource();
        var gameTitle = "someGameTitle";
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                source, "someUrl", "someTitle", "someOriginalFileName",
                null, gameTitle, "someGameId", "someVersion", "5 KB", null,
                null, FileBackupStatus.DISCOVERED, null);
        var tempFilePath = "someFileDir/someFile";

        when(filePathProvider.createTemporaryFilePath(source, gameTitle))
                .thenReturn(tempFilePath);

        fileManager.setAvailableSizeInBytes(0);

        FileBackupFailedException exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileVersionBackup));
        assertEquals(NotEnoughFreeSpaceException.class, exception.getCause().getClass());
    }

    @Test
    void isReadyForShouldReturnTrueIfFileIsReadyToDownload() {
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "5 KB", null,
                null, FileBackupStatus.DISCOVERED, null);

        when(sourceFileBackupService.isReady())
                .thenReturn(true);

        assertTrue(fileBackupService.isReadyFor(gameFileVersionBackup));
    }

    @Test
    void isReadyForShouldReturnFalseIfFileIsNotReadyToDownload() {
        var gameFileVersionBackup = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "5 KB", null,
                null, FileBackupStatus.DISCOVERED, null);

        when(sourceFileBackupService.isReady())
                .thenReturn(false);

        assertFalse(fileBackupService.isReadyFor(gameFileVersionBackup));
    }
}