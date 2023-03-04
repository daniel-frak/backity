package dev.codesoapbox.backity.core.files.domain.downloading.services;

import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.FileDownloadFailedException;
import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.GameFileDownloadUrlEmptyException;
import dev.codesoapbox.backity.core.files.domain.downloading.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.files.domain.downloading.model.FileStatus;
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

    public void downloadGameFile(GameFileVersion gameFileVersion) {
        log.info("Downloading game file {} (url={})...", gameFileVersion.getId(), gameFileVersion.getUrl());

        try {
            markInProgress(gameFileVersion);
            validateReadyForDownload(gameFileVersion);
            String tempFilePath = createTemporaryFilePath(gameFileVersion);
            validateEnoughFreeSpaceOnDisk(tempFilePath, gameFileVersion.getSize());
            tryToDownload(gameFileVersion, tempFilePath);
        } catch (IOException | RuntimeException e) {
            markFailed(gameFileVersion, e);
            throw new FileDownloadFailedException(gameFileVersion, e);
        }
    }

    private void markInProgress(GameFileVersion gameFileVersion) {
        gameFileVersion.setStatus(FileStatus.DOWNLOAD_IN_PROGRESS);
        gameFileVersionRepository.save(gameFileVersion);
    }

    private void validateReadyForDownload(GameFileVersion gameFileVersion) {
        if (Strings.isBlank(gameFileVersion.getUrl())) {
            throw new GameFileDownloadUrlEmptyException(gameFileVersion.getId());
        }
    }

    private String createTemporaryFilePath(GameFileVersion gameFileVersion) throws IOException {
        return filePathProvider.createTemporaryFilePath(
                gameFileVersion.getSource(), gameFileVersion.getGameTitle());
    }

    private void validateEnoughFreeSpaceOnDisk(String filePath, String size) {
        // @TODO Get free up-to-date filesize from URL header!
        Long sizeInBytes = new FileSizeAccumulator().add(size).getInBytes();
        if (!fileManager.isEnoughFreeSpaceOnDisk(sizeInBytes, filePath)) {
            throw new NotEnoughFreeSpaceException(filePath);
        }
    }

    private void tryToDownload(GameFileVersion gameFileVersion, String tempFilePath) throws IOException {
        try {
            updateFilePath(gameFileVersion, tempFilePath);
            String downloadedPath = downloadToDisk(gameFileVersion, tempFilePath);
            markDownloaded(gameFileVersion, downloadedPath);
        } catch (IOException e) {
            tryToCleanUpAfterFailedDownload(gameFileVersion, tempFilePath);
            throw e;
        }
    }

    private void updateFilePath(GameFileVersion gameFileVersion, String tempFilePath) {
        gameFileVersion.setFilePath(tempFilePath);
        gameFileVersionRepository.save(gameFileVersion);
    }

    /**
     * @return the path of the downloaded file
     */
    private String downloadToDisk(GameFileVersion gameFileVersion, String tempFilePath) throws IOException {
        SourceFileDownloader sourceDownloader = getSourceDownloader(gameFileVersion.getSource());
        return sourceDownloader.downloadGameFile(gameFileVersion, tempFilePath);
    }

    private void markDownloaded(GameFileVersion gameFileVersion, String downloadedPath) {
        gameFileVersion.markAsDownloaded(downloadedPath);
        gameFileVersionRepository.save(gameFileVersion);
    }

    private void tryToCleanUpAfterFailedDownload(GameFileVersion gameFileVersion,
                                                 String tempFilePath) throws IOException {
        fileManager.deleteIfExists(tempFilePath);
        if (tempFilePath.equals(gameFileVersion.getFilePath())) {
            gameFileVersion.setFilePath(null);
            gameFileVersionRepository.save(gameFileVersion);
        }
    }

    private SourceFileDownloader getSourceDownloader(String source) {
        if (!sourceFileDownloaders.containsKey(source)) {
            throw new IllegalArgumentException("File downloader for source not found: " + source);
        }

        return sourceFileDownloaders.get(source);
    }

    private void markFailed(GameFileVersion gameFileVersion, Exception e) {
        gameFileVersion.fail(e.getMessage());
        gameFileVersionRepository.save(gameFileVersion);
    }

    public boolean isReadyFor(GameFileVersion gameFileVersion) {
        return getSourceDownloader(gameFileVersion.getSource()).isReady();
    }
}
