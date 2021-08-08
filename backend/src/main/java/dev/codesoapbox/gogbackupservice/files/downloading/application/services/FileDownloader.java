package dev.codesoapbox.gogbackupservice.files.downloading.application.services;

import dev.codesoapbox.gogbackupservice.integrations.gog.application.services.embed.GogEmbedClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static java.nio.file.StandardOpenOption.CREATE;

@Slf4j
@Service
public class FileDownloader {

    private final GogEmbedClient gogEmbedClient;
    private final String defaultPathTemplate;

    public FileDownloader(GogEmbedClient gogEmbedClient,
                          @Value("${default-path-template}") String defaultPathTemplate) {
        this.gogEmbedClient = gogEmbedClient;
        this.defaultPathTemplate = defaultPathTemplate;
    }

    public void downloadGameFile(String gameTitle, String url, Long sizeInBytes) {
        if (Strings.isBlank(url)) {
            throw new IllegalArgumentException("Game file url must not be null or empty");
        }

        log.info("Downloading game file {}...", url);

        String tempFileName = "TEMP_" + UUID.randomUUID();
        String tempFilePath = prepareFilePath(gameTitle, tempFileName);

        createDirectories(tempFilePath);
        validateEnoughFreeSpaceOnDisk(sizeInBytes, tempFilePath);

        AtomicReference<String> newFileName = new AtomicReference<>();
        AtomicLong sizeInBytesFromRequest = new AtomicLong();

        final Flux<DataBuffer> dataBufferFlux = gogEmbedClient.getFileBuffer(url, newFileName, sizeInBytesFromRequest);

        final Path path = FileSystems.getDefault().getPath(tempFilePath);
        DataBufferUtils
                .write(dataBufferFlux, path, CREATE)
                .share()
                .block();

        log.info("Downloaded file {} to {}", url, tempFilePath);

        validateFileSize(tempFilePath, sizeInBytesFromRequest);

        String newFilePath = prepareFilePath(gameTitle, newFileName.get());
        renameFile(tempFilePath, newFilePath);
    }

    private String prepareFilePath(String gameTitle, String fileName) {
        return defaultPathTemplate
                .replace("{SOURCE}", "GOG") // @TODO Extract source information
                .replace("{TITLE}", gameTitle)
                .replace("{FILENAME}", fileName);
    }

    private void validateEnoughFreeSpaceOnDisk(Long sizeInBytes, String filePath) {
        File file = new File(extractDirectory(filePath));
        if(file.getUsableSpace() < sizeInBytes) {
            throw new RuntimeException("Not enough space left to save: " + filePath);
        }
    }

    private void validateFileSize(String tempFilePath, AtomicLong size) {
        File downloadedFile = new File(tempFilePath);
        if (downloadedFile.length() != size.get()) {
            throw new RuntimeException("The downloaded size of " + tempFilePath + "is not what was expected ("
                    + downloadedFile.length() + " vs " + size.get() + ")");
        } else {
            log.info("Filesize check for {} passed successfully", tempFilePath);
        }
    }

    private void createDirectories(String tempFilePath) {
        String pathDirectory = extractDirectory(tempFilePath);
        try {
            Files.createDirectories(FileSystems.getDefault().getPath(pathDirectory));
        } catch (IOException e) {
            log.error("Could not create path: " + pathDirectory, e);
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

    private String extractDirectory(String path) {
        return path.substring(0, path.lastIndexOf('/'));
    }
}
