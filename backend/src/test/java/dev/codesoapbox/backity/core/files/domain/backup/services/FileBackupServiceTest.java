package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.backup.fakes.FakeUnixFileManager;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupUrlEmptyException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails;
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
                .thenReturn("someSourceId");
        fileManager = new FakeUnixFileManager(5000);
        fileBackupService = new FileBackupService(filePathProvider, gameFileDetailsRepository, fileManager,
                singletonList(sourceFileBackupService));
    }

    @Test
    void shouldDownloadGameFile() throws IOException {
        String source = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = TestGameFileDetails.discovered().build();
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
        GameFileDetails gameFileDetails = TestGameFileDetails.discovered().build();
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
        GameFileDetails gameFileDetails = TestGameFileDetails.discovered()
                .url(url)
                .build();
        FileBackupFailedException exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileDetails));
        assertEquals(FileBackupUrlEmptyException.class, exception.getCause().getClass());
    }

    @Test
    void downloadGameFileShouldThrowIfSourceDownloaderNotFound() throws IOException {
        var sourceId = "nonExistentSource1";
        GameFileDetails gameFileDetails = TestGameFileDetails.discovered()
                .sourceId(sourceId)
                .build();
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
        String sourceId = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = TestGameFileDetails.discovered()
                .sourceId(sourceId)
                .build();

        when(filePathProvider.createTemporaryFilePath(sourceId,
                gameFileDetails.getSourceFileDetails().originalGameTitle()))
                .thenThrow(new IOException());

        FileBackupFailedException exception = assertThrows(FileBackupFailedException.class,
                () -> fileBackupService.backUpGameFile(gameFileDetails));
        assertEquals(IOException.class, exception.getCause().getClass());
    }

    @Test
    void downloadGameFileShouldThrowIfNotEnoughFreeSpace() throws IOException {
        String sourceId = sourceFileBackupService.getSource();
        GameFileDetails gameFileDetails = TestGameFileDetails.discovered()
                .sourceId(sourceId)
                .build();
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
        GameFileDetails gameFileDetails = TestGameFileDetails.discovered().build();

        when(sourceFileBackupService.isReady())
                .thenReturn(true);

        assertTrue(fileBackupService.isReadyFor(gameFileDetails));
    }

    @Test
    void isReadyForShouldReturnFalseIfFileIsNotReadyToDownload() {
        GameFileDetails gameFileDetails = TestGameFileDetails.discovered().build();

        when(sourceFileBackupService.isReady())
                .thenReturn(false);

        assertFalse(fileBackupService.isReadyFor(gameFileDetails));
    }
}