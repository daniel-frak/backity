package dev.codesoapbox.backity.integrations.gog.application.services;

import dev.codesoapbox.backity.core.files.downloading.application.services.FilePathProvider;
import dev.codesoapbox.backity.core.files.downloading.domain.services.SourceFileDownloader;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.integrations.gog.application.services.auth.GogAuthService;
import dev.codesoapbox.backity.integrations.gog.application.services.embed.GogEmbedClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.file.StandardOpenOption.CREATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class GogFileDownloader implements SourceFileDownloader {

    private final GogEmbedClient gogEmbedClient;
    private final GogAuthService authService;
    private final FilePathProvider filePathProvider;

    @Getter
    private final String source = "GOG";

    @Override
    public void downloadGameFile(EnqueuedFileDownload enqueuedFileDownload, String tempFilePath) {
        AtomicReference<String> targetFileName = new AtomicReference<>();
        AtomicLong sizeInBytesFromRequest = new AtomicLong();

        final Flux<DataBuffer> dataBufferFlux = gogEmbedClient.getFileBuffer(enqueuedFileDownload.getUrl(),
                targetFileName, sizeInBytesFromRequest);

        final Path path = FileSystems.getDefault().getPath(tempFilePath);
        DataBufferUtils
                .write(dataBufferFlux, path, CREATE)
                .share()
                .block();

        log.info("Downloaded file {} to {}", enqueuedFileDownload.getUrl(), tempFilePath);

        validateDownloadedFileSize(tempFilePath, sizeInBytesFromRequest);

        String newFilePath = filePathProvider.getFilePath(enqueuedFileDownload.getGameTitle(), targetFileName.get(),
                enqueuedFileDownload.getSource());
        renameFile(tempFilePath, newFilePath);
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

    private void renameFile(String tempFilePath, String newFilePath) {
        Path originalPath = Paths.get(tempFilePath);
        Path newPath = Paths.get(newFilePath);
        try {
            Files.move(originalPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Renamed file {} to {}", tempFilePath, newFilePath);
        } catch (IOException e) {
            log.error("Could not rename file: " + tempFilePath, e);
        }
    }
}
