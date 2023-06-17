package dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services;

import dev.codesoapbox.backity.core.files.adapters.driven.files.RealFileManager;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.services.BackupProgress;
import dev.codesoapbox.backity.core.files.domain.backup.services.FileManager;
import dev.codesoapbox.backity.core.files.domain.discovery.model.ProgressInfo;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.FileBackupException;
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

import static dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails.discovered;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class UrlFileBackupServiceTest {

    private UrlFileDownloader urlFileDownloader;
    private List<ProgressInfo> progressHistory;

    @BeforeEach
    void setUp() {
        progressHistory = new ArrayList<>();
    }

    @Test
    void shouldDownloadGameFileWithProgressTracking(@TempDir Path tempDir) throws IOException {
        urlFileDownloader = new UrlFileDownloader(new RealFileManager(), progressHistory::add);
        GameFileDetails gameFileDetails = discovered().build();
        FileBufferProviderStub fileBufferProvider = new FileBufferProviderStub();

        urlFileDownloader.downloadGameFile(fileBufferProvider, gameFileDetails,
                tempDir + File.separator + "tempFile");

        assertTrue(new File(tempDir + File.separator
                + gameFileDetails.getSourceFileDetails().originalFileName()).exists());
        assertEquals(100, progressHistory.get(0).percentage());
    }

    @Test
    void downloadGameFileShouldThrowWhenFileNotFound() throws IOException {
        FileManager fileManager = mock(FileManager.class);
        urlFileDownloader = new UrlFileDownloader(fileManager, i -> {
        });

        GameFileDetails gameFileDetails = discovered().build();
        FileBufferProviderStub fileBufferProvider = new FileBufferProviderStub();

        doThrow(new FileNotFoundException())
                .when(fileManager).getOutputStream(any());

        var exception = assertThrows(FileBackupException.class,
                () -> urlFileDownloader.downloadGameFile(fileBufferProvider, gameFileDetails,
                        "badFilePath"));
        assertTrue(exception.getMessage().contains("Unable to create file"));
    }

    @Test
    void downloadGameFileShouldThrowWhenFileSizeIsInvalid(@TempDir Path tempDir) {
        urlFileDownloader = new UrlFileDownloader(new RealFileManager(), progressHistory::add);
        GameFileDetails gameFileDetails = discovered().build();
        FileBufferProviderStub fileBufferProvider = new FileBufferProviderStub();
        fileBufferProvider.setGiveIncorrectFileSize(true);

        var exception = assertThrows(FileBackupException.class,
                () -> urlFileDownloader.downloadGameFile(fileBufferProvider, gameFileDetails,
                        tempDir + File.separator + "tempFile"));
        assertTrue(exception.getMessage().contains("The downloaded size of"));
    }

    @RequiredArgsConstructor
    private static class FileBufferProviderStub implements FileBufferProvider {

        @Setter
        private boolean giveIncorrectFileSize;

        @Override
        public Flux<DataBuffer> getFileBuffer(String gameFileUrl,
                                              BackupProgress progress) {
            byte[] bytes = "abcd".getBytes(StandardCharsets.UTF_8);
            if (giveIncorrectFileSize) {
                progress.startTracking(bytes.length + 1);
            } else {
                progress.startTracking(bytes.length);
            }
            DefaultDataBuffer dataBuffer = DefaultDataBufferFactory.sharedInstance.wrap(ByteBuffer.wrap(bytes));
            return Flux.just(dataBuffer);
        }
    }
}