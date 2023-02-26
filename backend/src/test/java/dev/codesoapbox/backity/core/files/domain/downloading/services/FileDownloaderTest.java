package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.FileDownloadFailedException;
import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.GameFileDownloadUrlEmptyException;
import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.downloading.fakes.FakeUnixFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileDownloaderTest {

    private FileDownloader fileDownloader;

    @Mock
    private FilePathProvider filePathProvider;

    @Mock
    private SourceFileDownloader sourceFileDownloader;

    private FakeUnixFileManager fileManager;

    @BeforeEach
    void setUp() {
        when(sourceFileDownloader.getSource())
                .thenReturn("someSource");
        fileManager = new FakeUnixFileManager(5000);
        fileDownloader = new FileDownloader(filePathProvider, fileManager, singletonList(sourceFileDownloader));
    }

    @Test
    void shouldDownloadGameFile() throws IOException {
        String source = sourceFileDownloader.getSource();
        var gameTitle = "someGameTitle";
        var enqueuedFileDownload = GameFileVersion.builder()
                .gameTitle(gameTitle)
                .source(source)
                .url("someUrl")
                .size("5 KB")
                .build();
        var tempFilePath = "someFileDir/someFile";

        when(filePathProvider.createTemporaryFilePath(source, gameTitle))
                .thenReturn(tempFilePath);

        fileDownloader.downloadGameFile(enqueuedFileDownload);

        assertTrue(fileManager.freeSpaceWasCheckedFor(tempFilePath));
        verify(sourceFileDownloader).downloadGameFile(enqueuedFileDownload, tempFilePath);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void downloadGameFileShouldThrowIfUrlIsNullOrEmpty(String url) {
        GameFileVersion gameFileVersion = GameFileVersion.builder()
                .url(url)
                .build();

        FileDownloadFailedException exception = assertThrows(FileDownloadFailedException.class,
                () -> fileDownloader.downloadGameFile(gameFileVersion));
        assertEquals(GameFileDownloadUrlEmptyException.class, exception.getCause().getClass());
    }

    @Test
    void downloadGameFileShouldThrowIfSourceDownloaderNotFound() throws IOException {
        var source = "nonExistentSource";
        var gameTitle = "someGameTitle";
        var enqueuedFileDownload = GameFileVersion.builder()
                .gameTitle(gameTitle)
                .source(source)
                .url("someUrl")
                .size("5 KB")
                .build();
        var tempFilePath = "someFileDir/someFile";

        lenient().when(filePathProvider.createTemporaryFilePath(eq(source), eq(gameTitle)))
                .thenReturn(tempFilePath);

        FileDownloadFailedException exception = assertThrows(FileDownloadFailedException.class,
                () -> fileDownloader.downloadGameFile(enqueuedFileDownload));
        assertEquals(IllegalArgumentException.class, exception.getCause().getClass());

    }

    @Test
    void downloadGameFileShouldThrowIfIOExceptionOccurs() throws IOException {
        var source = sourceFileDownloader.getSource();
        var gameTitle = "someGameTitle";
        var enqueuedFileDownload = GameFileVersion.builder()
                .gameTitle(gameTitle)
                .source(source)
                .url("someUrl")
                .size("5 KB")
                .build();

        when(filePathProvider.createTemporaryFilePath(source, gameTitle))
                .thenThrow(new IOException());

        FileDownloadFailedException exception = assertThrows(FileDownloadFailedException.class,
                () -> fileDownloader.downloadGameFile(enqueuedFileDownload));
        assertEquals(IOException.class, exception.getCause().getClass());
    }

    @Test
    void downloadGameFileShouldThrowIfNotEnoughFreeSpace() throws IOException {
        var source = sourceFileDownloader.getSource();
        var gameTitle = "someGameTitle";
        var enqueuedFileDownload = GameFileVersion.builder()
                .gameTitle(gameTitle)
                .source(source)
                .url("someUrl")
                .size("5 KB")
                .build();
        var tempFilePath = "someFileDir/someFile";

        when(filePathProvider.createTemporaryFilePath(source, gameTitle))
                .thenReturn(tempFilePath);

        fileManager.setAvailableSizeInBytes(0);

        FileDownloadFailedException exception = assertThrows(FileDownloadFailedException.class,
                () -> fileDownloader.downloadGameFile(enqueuedFileDownload));
        assertEquals(NotEnoughFreeSpaceException.class, exception.getCause().getClass());
    }

    @Test
    void isReadyForShouldReturnTrueIfFileIsReadyToDownload() {
        var enqueuedFileDownload = GameFileVersion.builder()
                .source(sourceFileDownloader.getSource())
                .build();

        when(sourceFileDownloader.isReady())
                .thenReturn(true);

        assertTrue(fileDownloader.isReadyFor(enqueuedFileDownload));
    }

    @Test
    void isReadyForShouldReturnFalseIfFileIsNotReadyToDownload() {
        var enqueuedFileDownload = GameFileVersion.builder()
                .source(sourceFileDownloader.getSource())
                .build();

        when(sourceFileDownloader.isReady())
                .thenReturn(false);

        assertFalse(fileDownloader.isReadyFor(enqueuedFileDownload));
    }
}