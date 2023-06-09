package dev.codesoapbox.backity.core.files.domain.backup.services;

import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.FileBackupUrlEmptyException;
import dev.codesoapbox.backity.core.files.domain.backup.exceptions.NotEnoughFreeSpaceException;
import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionBackupRepository;
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
public class FileBackupService {

    private final FilePathProvider filePathProvider;
    private final GameFileVersionBackupRepository gameFileVersionBackupRepository;
    private final FileManager fileManager;
    private final Map<String, SourceFileBackupService> sourceFileDownloaders;

    public FileBackupService(FilePathProvider filePathProvider, GameFileVersionBackupRepository gameFileVersionBackupRepository,
                             FileManager fileManager, List<SourceFileBackupService> sourceFileBackupServices) {
        this.filePathProvider = filePathProvider;
        this.gameFileVersionBackupRepository = gameFileVersionBackupRepository;
        this.fileManager = fileManager;
        this.sourceFileDownloaders = sourceFileBackupServices.stream()
                .collect(Collectors.toMap(SourceFileBackupService::getSource, d -> d));
    }

    public void backUpGameFile(GameFileVersionBackup gameFileVersionBackup) {
        log.info("Backing up game file {} (url={})...", gameFileVersionBackup.getId(), gameFileVersionBackup.getUrl());

        try {
            markInProgress(gameFileVersionBackup);
            validateReadyForDownload(gameFileVersionBackup);
            String tempFilePath = createTemporaryFilePath(gameFileVersionBackup);
            validateEnoughFreeSpaceOnDisk(tempFilePath, gameFileVersionBackup.getSize());
            tryToBackUp(gameFileVersionBackup, tempFilePath);
        } catch (IOException | RuntimeException e) {
            markFailed(gameFileVersionBackup, e);
            throw new FileBackupFailedException(gameFileVersionBackup, e);
        }
    }

    private void markInProgress(GameFileVersionBackup gameFileVersionBackup) {
        gameFileVersionBackup.setStatus(FileBackupStatus.IN_PROGRESS);
        gameFileVersionBackupRepository.save(gameFileVersionBackup);
    }

    private void validateReadyForDownload(GameFileVersionBackup gameFileVersionBackup) {
        if (Strings.isBlank(gameFileVersionBackup.getUrl())) {
            throw new FileBackupUrlEmptyException(gameFileVersionBackup.getId());
        }
    }

    private String createTemporaryFilePath(GameFileVersionBackup gameFileVersionBackup) throws IOException {
        return filePathProvider.createTemporaryFilePath(
                gameFileVersionBackup.getSource(), gameFileVersionBackup.getGameTitle());
    }

    private void validateEnoughFreeSpaceOnDisk(String filePath, String size) {
        // @TODO Get free up-to-date filesize from URL header!
        Long sizeInBytes = new FileSizeAccumulator().add(size).getInBytes();
        if (!fileManager.isEnoughFreeSpaceOnDisk(sizeInBytes, filePath)) {
            throw new NotEnoughFreeSpaceException(filePath);
        }
    }

    private void tryToBackUp(GameFileVersionBackup gameFileVersionBackup, String tempFilePath) throws IOException {
        try {
            updateFilePath(gameFileVersionBackup, tempFilePath);
            String downloadedPath = downloadToDisk(gameFileVersionBackup, tempFilePath);
            markDownloaded(gameFileVersionBackup, downloadedPath);
        } catch (IOException e) {
            tryToCleanUpAfterFailedDownload(gameFileVersionBackup, tempFilePath);
            throw e;
        }
    }

    private void updateFilePath(GameFileVersionBackup gameFileVersionBackup, String tempFilePath) {
        gameFileVersionBackup.setFilePath(tempFilePath);
        gameFileVersionBackupRepository.save(gameFileVersionBackup);
    }

    /**
     * @return the path of the downloaded file
     */
    private String downloadToDisk(GameFileVersionBackup gameFileVersionBackup, String tempFilePath) throws IOException {
        SourceFileBackupService sourceDownloader = getSourceDownloader(gameFileVersionBackup.getSource());
        return sourceDownloader.backUpGameFile(gameFileVersionBackup, tempFilePath);
    }

    private void markDownloaded(GameFileVersionBackup gameFileVersionBackup, String downloadedPath) {
        gameFileVersionBackup.markAsDownloaded(downloadedPath);
        gameFileVersionBackupRepository.save(gameFileVersionBackup);
    }

    private void tryToCleanUpAfterFailedDownload(GameFileVersionBackup gameFileVersionBackup,
                                                 String tempFilePath) throws IOException {
        fileManager.deleteIfExists(tempFilePath);
        if (tempFilePath.equals(gameFileVersionBackup.getFilePath())) {
            gameFileVersionBackup.setFilePath(null);
            gameFileVersionBackupRepository.save(gameFileVersionBackup);
        }
    }

    private SourceFileBackupService getSourceDownloader(String source) {
        if (!sourceFileDownloaders.containsKey(source)) {
            throw new IllegalArgumentException("File downloader for source not found: " + source);
        }

        return sourceFileDownloaders.get(source);
    }

    private void markFailed(GameFileVersionBackup gameFileVersionBackup, Exception e) {
        gameFileVersionBackup.fail(e.getMessage());
        gameFileVersionBackupRepository.save(gameFileVersionBackup);
    }

    public boolean isReadyFor(GameFileVersionBackup gameFileVersionBackup) {
        return getSourceDownloader(gameFileVersionBackup.getSource()).isReady();
    }
}
