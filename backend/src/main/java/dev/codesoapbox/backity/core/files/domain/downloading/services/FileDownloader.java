package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.FileDownloadFailedException;
import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.GameFileDownloadUrlEmptyException;
import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
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

    public String downloadGameFile(GameFileVersion gameFileVersion) {
        log.info("Downloading game file {} (url={})...", gameFileVersion.getId(), gameFileVersion.getUrl());

        try {
            validateFileDownload(gameFileVersion);

            String tempFilePath = filePathProvider.createTemporaryFilePath(
                    gameFileVersion.getSource(), gameFileVersion.getGameTitle());
            validateEnoughFreeSpaceOnDisk(tempFilePath, gameFileVersion.getSize());
            // @TODO Write test for return value
            return downloadToDisk(gameFileVersion, tempFilePath);
        } catch (IOException | RuntimeException e) {
            throw new FileDownloadFailedException(gameFileVersion, e);
        }
    }

    private void validateFileDownload(GameFileVersion gameFileVersion) {
        if (Strings.isBlank(gameFileVersion.getUrl())) {
            throw new GameFileDownloadUrlEmptyException(gameFileVersion.getId());
        }
    }

    private void validateEnoughFreeSpaceOnDisk(String filePath, String size) {
        Long sizeInBytes = new FileSizeAccumulator().add(size).getInBytes();
        if (!fileManager.isEnoughFreeSpaceOnDisk(sizeInBytes, filePath)) {
            throw new NotEnoughFreeSpaceException(filePath);
        }
    }

    private String downloadToDisk(GameFileVersion gameFileVersion, String tempFilePath) throws IOException {
        SourceFileDownloader sourceDownloader = getSourceDownloader(gameFileVersion.getSource());
        // @TODO Write test for return value
        return sourceDownloader.downloadGameFile(gameFileVersion, tempFilePath);
    }

    private SourceFileDownloader getSourceDownloader(String source) {
        if (!sourceFileDownloaders.containsKey(source)) {
            throw new IllegalArgumentException("File downloader for source not found: " + source);
        }

        return sourceFileDownloaders.get(source);
    }

    public boolean isReadyFor(GameFileVersion gameFileVersion) {
        return getSourceDownloader(gameFileVersion.getSource()).isReady();
    }
}
