package dev.codesoapbox.backity.core.files.downloading.application.services;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.services.SourceFileDownloader;
import dev.codesoapbox.backity.integrations.gog.application.services.embed.FileSizeAccumulator;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Wrapper for all source file downloaders.
 * <p>
 * Downloads files from remote servers.
 */
@Slf4j
@Service
public class FileDownloader {

    private final FilePathProvider filePathProvider;
    private final Map<String, SourceFileDownloader> fileDownloaders;

    public FileDownloader(FilePathProvider filePathProvider, List<SourceFileDownloader> fileDownloaders) {
        this.filePathProvider = filePathProvider;
        this.fileDownloaders = fileDownloaders.stream()
                .collect(Collectors.toMap(SourceFileDownloader::getSource, d -> d));
    }

    public void downloadGameFile(EnqueuedFileDownload enqueuedFileDownload) {
        String url = enqueuedFileDownload.getUrl();
        log.info("Downloading game file {}...", url);

        if (Strings.isBlank(url)) {
            throw new IllegalArgumentException("Game file url must not be null or empty");
        }

        String tempFilePath = createTempFilePath(enqueuedFileDownload);
        validateEnoughFreeSpaceOnDisk(enqueuedFileDownload.getSize(), tempFilePath);

        getSourceDownloader(enqueuedFileDownload.getSource())
                .downloadGameFile(enqueuedFileDownload, tempFilePath);
    }

    private String createTempFilePath(EnqueuedFileDownload enqueuedFileDownload) {
        String tempFileName = "TEMP_" + UUID.randomUUID();
        String tempFilePath = filePathProvider.getFilePath(enqueuedFileDownload.getGameTitle(), tempFileName,
                enqueuedFileDownload.getSource());
        createDirectories(tempFilePath);
        return tempFilePath;
    }

    private void createDirectories(String tempFilePath) {
        String pathDirectory = extractDirectory(tempFilePath);
        try {
            Files.createDirectories(FileSystems.getDefault().getPath(pathDirectory));
        } catch (IOException e) {
            log.error("Could not create path: " + pathDirectory, e);
        }
    }

    private String extractDirectory(String path) {
        return path.substring(0, path.lastIndexOf('/'));
    }

    private void validateEnoughFreeSpaceOnDisk(String size, String filePath) {
        Long sizeInBytes = new FileSizeAccumulator().add(size).getInBytes();
        File file = new File(extractDirectory(filePath));
        if (file.getUsableSpace() < sizeInBytes) {
            throw new RuntimeException("Not enough space left to save: " + filePath);
        }
    }

    private SourceFileDownloader getSourceDownloader(String source) {
        if(!fileDownloaders.containsKey(source)) {
            throw new IllegalArgumentException("File downloader for source not found: " + source);
        }

        return fileDownloaders.get(source);
    }
}
