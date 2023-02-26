package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services;

import dev.codesoapbox.backity.core.files.adapters.driven.files.RealFileManager;
import dev.codesoapbox.backity.core.files.domain.discovery.model.ProgressInfo;
import dev.codesoapbox.backity.core.files.domain.downloading.services.DownloadProgress;
import dev.codesoapbox.backity.core.files.domain.downloading.services.FileManager;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.FileDownloadException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UrlFileDownloaderTest {

    private UrlFileDownloader urlFileDownloader;
    private List<ProgressInfo> progressHistory;

    @BeforeEach
    void setUp() {
        progressHistory = new ArrayList<>();
    }

    @Test
    void shouldDownloadGameFileWithProgressTracking(@TempDir Path tempDir) throws IOException {
        urlFileDownloader = new UrlFileDownloader(new RealFileManager(), progressHistory::add);
        String gameFileUrl = "someUrl";
        FileBufferProviderStub fileBufferProvider = new FileBufferProviderStub();

        urlFileDownloader.downloadGameFile(fileBufferProvider, gameFileUrl,
                tempDir + File.separator + "tempFile");

        assertTrue(new File(tempDir + File.separator + fileBufferProvider.getFileName()).exists());
        assertEquals(100, progressHistory.get(0).percentage());
    }

    @Test
    void downloadGameFileShouldThrowWhenFileNotFound() throws IOException {
        FileManager fileManager = mock(FileManager.class);
        urlFileDownloader = new UrlFileDownloader(fileManager, i -> {
        });

        String gameFileUrl = "someUrl";
        FileBufferProviderStub fileBufferProvider = new FileBufferProviderStub();

        doThrow(new FileNotFoundException())
                .when(fileManager).getOutputStream(any());

        var exception = assertThrows(FileDownloadException.class,
                () -> urlFileDownloader.downloadGameFile(fileBufferProvider, gameFileUrl, "badFilePath"));
        assertTrue(exception.getMessage().contains("Unable to create file"));
    }

    @Test
    void downloadGameFileShouldThrowWhenFileSizeIsInvalid(@TempDir Path tempDir) {
        urlFileDownloader = new UrlFileDownloader(new RealFileManager(), progressHistory::add);
        String gameFileUrl = "someUrl";
        FileBufferProviderStub fileBufferProvider = new FileBufferProviderStub();
        fileBufferProvider.setGiveIncorrectFileSize(true);

        var exception = assertThrows(FileDownloadException.class,
                () -> urlFileDownloader.downloadGameFile(fileBufferProvider, gameFileUrl,
                        tempDir + File.separator + "tempFile"));
        assertTrue(exception.getMessage().contains("The downloaded size of"));
    }

    @RequiredArgsConstructor
    private static class FileBufferProviderStub implements FileBufferProvider {

        @Setter
        private boolean giveIncorrectFileSize;

        @Override
        public Flux<DataBuffer> getFileBuffer(String gameFileUrl, AtomicReference<String> targetFileName,
                                              DownloadProgress progress) {
            byte[] bytes = "abcd".getBytes(StandardCharsets.UTF_8);
            targetFileName.set(getFileName());
            if (giveIncorrectFileSize) {
                progress.startTracking(bytes.length + 1);
            } else {
                progress.startTracking(bytes.length);
            }
            DefaultDataBuffer dataBuffer = DefaultDataBufferFactory.sharedInstance.wrap(ByteBuffer.wrap(bytes));
            return Flux.just(dataBuffer);
        }

        public String getFileName() {
            return "targetFileName";
        }
    }
}