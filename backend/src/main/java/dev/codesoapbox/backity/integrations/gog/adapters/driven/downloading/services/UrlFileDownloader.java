package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.ProgressInfo;
import dev.codesoapbox.backity.core.files.downloading.domain.services.DownloadProgress;
import dev.codesoapbox.backity.core.files.downloading.domain.services.FileManager;
import dev.codesoapbox.backity.integrations.gog.domain.exceptions.FileDownloadException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class UrlFileDownloader {

    private final FileManager fileManager;

    public void downloadGameFile(FileBufferProvider fileBufferProvider, String url, String tempFilePath)
            throws IOException {
        var targetFileName = new AtomicReference<String>();
        var progress = new DownloadProgress();
        Flux<DataBuffer> dataBufferFlux = fileBufferProvider.getFileBuffer(url, targetFileName, progress);

        writeToDisk(dataBufferFlux, tempFilePath, progress);

        log.info("Downloaded file {} to {}", url, tempFilePath);

        validateDownloadedFileSize(tempFilePath, progress.getContentLengthBytes());
        fileManager.renameFile(tempFilePath, targetFileName.get());
    }

    private void writeToDisk(Flux<DataBuffer> dataBufferFlux, String tempFilePath, DownloadProgress progress)
            throws IOException {
        Path path = FileSystems.getDefault().getPath(tempFilePath);

        Consumer<ProgressInfo> progressInfoConsumer = i -> log.info("File download progress: " + i);
        try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
            progress.subscribeToProgress(progressInfoConsumer);

            DataBufferUtils
                    .write(dataBufferFlux, progress.getTrackedOutputStream(fileOutputStream))
                    .blockLast();
        } catch (FileNotFoundException e) {
            throw new FileDownloadException("Unable to create file", e);
        } finally {
            progress.unsubscribeFromProgress(progressInfoConsumer);
        }
    }

    private void validateDownloadedFileSize(String tempFilePath, long sizeInBytes) {
        File downloadedFile = new File(tempFilePath);
        if (downloadedFile.length() != sizeInBytes) {
            throw new FileDownloadException("The downloaded size of " + tempFilePath + "is not what was expected ("
                    + downloadedFile.length() + " vs " + sizeInBytes + ")");
        } else {
            log.info("Filesize check for {} passed successfully", tempFilePath);
        }
    }
}
