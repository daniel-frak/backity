package dev.codesoapbox.backity.core.files.downloading.domain.services;

import dev.codesoapbox.backity.core.files.downloading.domain.exceptions.EnqueuedFileDownloadUrlEmptyException;
import dev.codesoapbox.backity.core.files.downloading.domain.exceptions.FileDownloadFailedException;
import dev.codesoapbox.backity.core.files.downloading.domain.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wrapper for all source file downloaders.
 * <p>
 * Downloads files from remote servers.
 */
@Slf4j
public class FileDownloader {

    private final FilePathProvider filePathProvider;
    private final FileManager fileManager;
    private final Map<String, SourceFileDownloader> sourceFileDownloaders;

    public FileDownloader(FilePathProvider filePathProvider, FileManager fileManager,
                          List<SourceFileDownloader> sourceFileDownloaders) {
        this.filePathProvider = filePathProvider;
        this.fileManager = fileManager;
        this.sourceFileDownloaders = sourceFileDownloaders.stream()
                .collect(Collectors.toMap(SourceFileDownloader::getSource, d -> d));
    }

    public void downloadGameFile(EnqueuedFileDownload enqueuedFileDownload) {
        log.info("Downloading game file {} (url={})...", enqueuedFileDownload.getId(), enqueuedFileDownload.getUrl());

        try {
            validateFileDownload(enqueuedFileDownload);

            String tempFilePath = filePathProvider.createTemporaryFilePath(
                    enqueuedFileDownload.getSource(), enqueuedFileDownload.getGameTitle());
            validateEnoughFreeSpaceOnDisk(tempFilePath, enqueuedFileDownload.getSize());

            downloadToDisk(enqueuedFileDownload, tempFilePath);
        } catch (IOException | RuntimeException e) {
            throw new FileDownloadFailedException(enqueuedFileDownload, e);
        }
    }

    private void validateFileDownload(EnqueuedFileDownload enqueuedFileDownload) {
        if (Strings.isBlank(enqueuedFileDownload.getUrl())) {
            throw new EnqueuedFileDownloadUrlEmptyException(enqueuedFileDownload.getId());
        }
    }

    private void validateEnoughFreeSpaceOnDisk(String filePath, String size) {
        Long sizeInBytes = new FileSizeAccumulator().add(size).getInBytes();
        if (!fileManager.isEnoughFreeSpaceOnDisk(sizeInBytes, filePath)) {
            throw new NotEnoughFreeSpaceException(filePath);
        }
    }

    private void downloadToDisk(EnqueuedFileDownload enqueuedFileDownload, String tempFilePath) throws IOException {
        SourceFileDownloader sourceDownloader = getSourceDownloader(enqueuedFileDownload.getSource());
        sourceDownloader.downloadGameFile(enqueuedFileDownload, tempFilePath);
    }

    private SourceFileDownloader getSourceDownloader(String source) {
        if (!sourceFileDownloaders.containsKey(source)) {
            throw new IllegalArgumentException("File downloader for source not found: " + source);
        }

        return sourceFileDownloaders.get(source);
    }

    public boolean isReadyFor(EnqueuedFileDownload enqueuedFileDownload) {
        return getSourceDownloader(enqueuedFileDownload.getSource()).isReady();
    }
}
