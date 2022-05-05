package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.downloading.domain.exceptions.EnqueuedFileDownloadUrlEmptyException;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileDownloaderTest {

    private FileDownloader fileDownloader;

    @Mock
    private FilePathProvider filePathProvider;

    @Mock
    private SourceFileDownloader sourceFileDownloader;

    @BeforeEach
    void setUp() {
        when(sourceFileDownloader.getSource())
                .thenReturn("someSource");
        fileDownloader = new FileDownloader(filePathProvider, singletonList(sourceFileDownloader));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void downloadGameFileShouldThrowIfUrlIsNullOrEmpty(String url) {
        EnqueuedFileDownload enqueuedFileDownload = EnqueuedFileDownload.builder()
                .url(url)
                .build();

        assertThrows(EnqueuedFileDownloadUrlEmptyException.class,
                () -> fileDownloader.downloadGameFile(enqueuedFileDownload));
    }

    @Test
    void shouldDownloadGameFile() {
        var gameTitle = "someGameTitle";
        var enqueuedFileDownload = EnqueuedFileDownload.builder()
                .gameTitle(gameTitle)
                .source(sourceFileDownloader.getSource())
                .url("someUrl")
                .size("5 KB")
                .build();
        var tempFileDirectory = "someFileDir";
        var tempFilePath = tempFileDirectory + "/someFile";
        String source = sourceFileDownloader.getSource();

        when(filePathProvider.getFilePath(eq(gameTitle), any(), eq(source)))
                .thenReturn(tempFilePath);

        fileDownloader.downloadGameFile(enqueuedFileDownload);

        assertTrue(Files.exists(Path.of(tempFileDirectory)));
        verify(sourceFileDownloader).downloadGameFile(enqueuedFileDownload, tempFilePath);
        // @TODO Finish me
    }

    @Test
    void isReadyForShouldReturnTrueIfFileIsReadyToDownload() {
        // @TODO Finish me
    }

    @Test
    void isReadyForShouldReturnFalseIfFileIsNotReadyToDownload() {
        // @TODO Finish me
    }
}