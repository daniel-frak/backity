package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.FileDownloadFailedException;
import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.GameFileDownloadUrlEmptyException;
import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.repositories.GameFileVersionRepository;
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
    private final GameFileVersionRepository gameFileVersionRepository;
    private final FileManager fileManager;
    private final Map<String, SourceFileDownloader> sourceFileDownloaders;

    public FileDownloader(FilePathProvider filePathProvider, GameFileVersionRepository gameFileVersionRepository,
                          FileManager fileManager, List<SourceFileDownloader> sourceFileDownloaders) {
        this.filePathProvider = filePathProvider;
        this.gameFileVersionRepository = gameFileVersionRepository;
        this.fileManager = fileManager;
        this.sourceFileDownloaders = sourceFileDownloaders.stream()
                .collect(Collectors.toMap(SourceFileDownloader::getSource, d -> d));
    }

    /**
     * @return the absolute file path of the downloaded file
     */
    public String downloadGameFile(GameFileVersion gameFileVersion) {
        log.info("Downloading game file {} (url={})...", gameFileVersion.getId(), gameFileVersion.getUrl());

        try {
            validateFileDownload(gameFileVersion);

            String tempFilePath = filePathProvider.createTemporaryFilePath(
                    gameFileVersion.getSource(), gameFileVersion.getGameTitle());

            validateEnoughFreeSpaceOnDisk(tempFilePath, gameFileVersion.getSize());

            try {
                updateFilePath(gameFileVersion, tempFilePath);
                return downloadToDisk(gameFileVersion, tempFilePath);
            } catch (IOException e) {
                tryToCleanUpAfterFailedDownload(gameFileVersion, tempFilePath);
                throw e;
            }
        } catch (IOException | RuntimeException e) {
            throw new FileDownloadFailedException(gameFileVersion, e);
        }
    }

    private void tryToCleanUpAfterFailedDownload(GameFileVersion gameFileVersion,
                                                        String tempFilePath) throws IOException {
        fileManager.deleteIfExists(tempFilePath);
        if (tempFilePath.equals(gameFileVersion.getFilePath())) {
            gameFileVersion.setFilePath(null);
            gameFileVersionRepository.save(gameFileVersion);
        }
    }

    private void updateFilePath(GameFileVersion gameFileVersion, String tempFilePath) {
        gameFileVersion.setFilePath(tempFilePath);
        gameFileVersionRepository.save(gameFileVersion);
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

    /**
     * @return the path of the downloaded file
     */
    private String downloadToDisk(GameFileVersion gameFileVersion, String tempFilePath) throws IOException {
        SourceFileDownloader sourceDownloader = getSourceDownloader(gameFileVersion.getSource());
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
