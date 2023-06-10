package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupUrlEmptyException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wrapper for all sourceId file downloaders.
 * <p>
 * Downloads files from remote servers.
 */
@Slf4j
public class FileBackupService {

    private final FilePathProvider filePathProvider;
    private final GameFileVersionRepository gameFileVersionRepository;
    private final FileManager fileManager;
    private final Map<String, SourceFileBackupService> sourceFileDownloaders;

    public FileBackupService(FilePathProvider filePathProvider, GameFileVersionRepository gameFileVersionRepository,
                             FileManager fileManager, List<SourceFileBackupService> sourceFileBackupServices) {
        this.filePathProvider = filePathProvider;
        this.gameFileVersionRepository = gameFileVersionRepository;
        this.fileManager = fileManager;
        this.sourceFileDownloaders = sourceFileBackupServices.stream()
                .collect(Collectors.toMap(SourceFileBackupService::getSource, d -> d));
    }

    public void backUpGameFile(GameFileVersion gameFileVersion) {
        log.info("Backing up game file {} (url={})...", gameFileVersion.getId(), gameFileVersion.getUrl());

        try {
            markInProgress(gameFileVersion);
            validateReadyForDownload(gameFileVersion);
            String tempFilePath = createTemporaryFilePath(gameFileVersion);
            validateEnoughFreeSpaceOnDisk(tempFilePath, gameFileVersion.getSize());
            tryToBackUp(gameFileVersion, tempFilePath);
        } catch (IOException | RuntimeException e) {
            markFailed(gameFileVersion, e);
            throw new FileBackupFailedException(gameFileVersion, e);
        }
    }

    private void markInProgress(GameFileVersion gameFileVersion) {
        gameFileVersion.setBackupStatus(FileBackupStatus.IN_PROGRESS);
        gameFileVersionRepository.save(gameFileVersion);
    }

    private void validateReadyForDownload(GameFileVersion gameFileVersion) {
        if (Strings.isBlank(gameFileVersion.getUrl())) {
            throw new FileBackupUrlEmptyException(gameFileVersion.getId());
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

    private void tryToBackUp(GameFileVersion gameFileVersion, String tempFilePath) throws IOException {
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
        SourceFileBackupService sourceDownloader = getSourceDownloader(gameFileVersion.getSource());
        return sourceDownloader.backUpGameFile(gameFileVersion, tempFilePath);
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

    private SourceFileBackupService getSourceDownloader(String source) {
        if (!sourceFileDownloaders.containsKey(source)) {
            throw new IllegalArgumentException("File downloader for sourceId not found: " + source);
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
