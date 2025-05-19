package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgressFactory;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePathProvider;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Wrapper for all gameProviderId file backup services.
 * <p>
 * Downloads files from remote servers.
 */
@Slf4j
public class FileBackupService {

    private final FilePathProvider filePathProvider;
    private final GameFileRepository gameFileRepository;
    private final StorageSolution storageSolution;
    private final Map<GameProviderId, GameProviderFileBackupService> gameProviderFileBackupServices;
    private final DownloadProgressFactory downloadProgressFactory;

    public FileBackupService(FilePathProvider filePathProvider, GameFileRepository gameFileRepository,
                             StorageSolution storageSolution,
                             List<GameProviderFileBackupService> gameProviderFileBackupServices,
                             DownloadProgressFactory downloadProgressFactory) {
        this.filePathProvider = filePathProvider;
        this.gameFileRepository = gameFileRepository;
        this.storageSolution = storageSolution;
        this.gameProviderFileBackupServices = gameProviderFileBackupServices.stream()
                .collect(Collectors.toMap(GameProviderFileBackupService::getGameProviderId,
                        d -> d));
        this.downloadProgressFactory = downloadProgressFactory;
    }

    public void backUpFile(GameFile gameFile) {
        log.info("Backing up game file {} (url={})...", gameFile.getId(),
                gameFile.getFileSource().url());

        try {
            markInProgress(gameFile);
            String filePath = buildFilePath(gameFile);
            tryToBackUp(gameFile, filePath);
        } catch (IOException | RuntimeException e) {
            markFailed(gameFile, e);
            throw new FileBackupFailedException(gameFile, e);
        }
    }

    private void markInProgress(GameFile gameFile) {
        gameFile.markAsInProgress();
        gameFileRepository.save(gameFile);
    }

    private String buildFilePath(GameFile gameFile) {
        return filePathProvider.buildUniqueFilePath(
                gameFile.getFileSource().gameProviderId(),
                gameFile.getFileSource().originalGameTitle(),
                gameFile.getFileSource().originalFileName());
    }

    private void tryToBackUp(GameFile gameFile, String filePath) throws IOException {
        try {
            updateFilePath(gameFile, filePath);
            downloadToDisk(gameFile);
            markDownloaded(gameFile, filePath);
        } catch (IOException e) {
            tryToCleanUpAfterFailedDownload(gameFile, filePath);
            throw e;
        }
    }

    private void updateFilePath(GameFile gameFile, String filePath) {
        gameFile.updateFilePath(filePath);
        gameFileRepository.save(gameFile);
    }

    private void downloadToDisk(GameFile gameFile) throws IOException {
        GameProviderId gameProviderId = gameFile.getFileSource().gameProviderId();
        GameProviderFileBackupService gameProviderFileBackupService = getGameProviderFileBackupService(gameProviderId);
        DownloadProgress downloadProgress = downloadProgressFactory.create();

        gameProviderFileBackupService.backUpFile(gameFile, downloadProgress);
    }

    private GameProviderFileBackupService getGameProviderFileBackupService(GameProviderId gameProviderId) {
        if (!gameProviderFileBackupServices.containsKey(gameProviderId)) {
            throw new IllegalArgumentException("File backup service for gameProviderId not found: " + gameProviderId);
        }

        return gameProviderFileBackupServices.get(gameProviderId);
    }

    private void markDownloaded(GameFile gameFile, String downloadedPath) {
        gameFile.markAsDownloaded(downloadedPath);
        gameFileRepository.save(gameFile);
    }

    private void tryToCleanUpAfterFailedDownload(GameFile gameFile, String filePath) {
        storageSolution.deleteIfExists(filePath);
        gameFile.clearFilePath();
        gameFileRepository.save(gameFile);
    }

    private void markFailed(GameFile gameFile, Exception exception) {
        String message = exception.getMessage();
        if(message == null) {
            message = "Unknown error";
        }
        gameFile.markAsFailed(message);
        gameFileRepository.save(gameFile);
    }

    public boolean isReadyFor(GameFile gameFile) {
        return getGameProviderFileBackupService(gameFile.getFileSource().gameProviderId()).isReady();
    }
}
