package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services;

import dev.codesoapbox.backity.core.files.discovery.domain.model.ProgressInfo;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.services.DownloadProgress;
import dev.codesoapbox.backity.core.files.downloading.domain.services.FileManager;
import dev.codesoapbox.backity.core.files.downloading.domain.services.SourceFileDownloader;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.exceptions.FileDownloadException;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.auth.GogAuthService;
import dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.services.embed.GogEmbedClient;
import lombok.Getter;
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
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class GogFileDownloader implements SourceFileDownloader {

    private final GogEmbedClient gogEmbedClient;
    private final GogAuthService authService;
    private final FileManager fileManager;

    @Getter
    private final String source = "GOG";

    @Override
    public void downloadGameFile(EnqueuedFileDownload enqueuedFileDownload, String tempFilePath) throws IOException {
        AtomicReference<String> targetFileName = new AtomicReference<>();
        AtomicLong sizeInBytesFromRequest = new AtomicLong();

        /*
        @TODO
            Extract the whole Flux<DataBuffer> + DownloadProgress logic to a generic file download service
            so that future integrations can reuse the code
         */
        final DownloadProgress progress = new DownloadProgress();

        final Flux<DataBuffer> dataBufferFlux = gogEmbedClient.getFileBuffer(enqueuedFileDownload.getUrl(),
                targetFileName, sizeInBytesFromRequest, progress);

        writeToDisk(dataBufferFlux, tempFilePath, progress);

        log.info("Downloaded file {} to {}", enqueuedFileDownload.getUrl(), tempFilePath);

        validateDownloadedFileSize(tempFilePath, sizeInBytesFromRequest);
        fileManager.renameFile(tempFilePath, targetFileName.get());
    }

    private void writeToDisk(Flux<DataBuffer> dataBufferFlux, String tempFilePath, DownloadProgress progress)
            throws IOException {
        final Path path = FileSystems.getDefault().getPath(tempFilePath);

        Consumer<ProgressInfo> progressInfoConsumer = i -> log.info("File download progress: " + i);
        try (FileOutputStream fileOutputStream = new FileOutputStream(path.toFile())) {
            progress.subscribeToProgress(progressInfoConsumer);

            DataBufferUtils
                    .write(dataBufferFlux, progress.getTrackedOutputStream(fileOutputStream))
                    .blockLast();
        } catch (FileNotFoundException e) {
            throw new FileDownloadException("Unable to create file", e);
        } finally {
            // @TODO: Do we really need to unsubscribe if DownloadProgress is just a temporary object...?
            progress.unsubscribeFromProgress(progressInfoConsumer);
        }
    }

    @Override
    public boolean isReady() {
        return authService.isAuthenticated();
    }

    private void validateDownloadedFileSize(String tempFilePath, AtomicLong size) {
        File downloadedFile = new File(tempFilePath);
        if (downloadedFile.length() != size.get()) {
            throw new RuntimeException("The downloaded size of " + tempFilePath + "is not what was expected ("
                    + downloadedFile.length() + " vs " + size.get() + ")");
        } else {
            log.info("Filesize check for {} passed successfully", tempFilePath);
        }
    }
}
