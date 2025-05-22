package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgressFactory;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
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

    private final UniqueFilePathResolver uniqueFilePathResolver;
    private final FileCopyRepository fileCopyRepository;
    private final BackupTargetRepository backupTargetRepository;
    private final StorageSolutionRepository storageSolutionRepository;
    private final Map<GameProviderId, GameProviderFileBackupService> gameProviderFileBackupServices;
    private final DownloadProgressFactory downloadProgressFactory;

    public FileBackupService(UniqueFilePathResolver uniqueFilePathResolver,
                             FileCopyRepository fileCopyRepository,
                             BackupTargetRepository backupTargetRepository,
                             StorageSolutionRepository storageSolutionRepository,
                             List<GameProviderFileBackupService> gameProviderFileBackupServices,
                             DownloadProgressFactory downloadProgressFactory) {
        this.uniqueFilePathResolver = uniqueFilePathResolver;
        this.fileCopyRepository = fileCopyRepository;
        this.backupTargetRepository = backupTargetRepository;
        this.storageSolutionRepository = storageSolutionRepository;
        this.gameProviderFileBackupServices = gameProviderFileBackupServices.stream()
                .collect(Collectors.toMap(GameProviderFileBackupService::getGameProviderId,
                        d -> d));
        this.downloadProgressFactory = downloadProgressFactory;
    }

    public void backUpFile(GameFile gameFile, FileCopy fileCopy) {
        log.info("Backing up game file {} (url={}, fileCopyId={})...", gameFile.getId(),
                gameFile.getFileSource().url(), fileCopy.getId());

        try {
            BackupTarget backupTarget = backupTargetRepository.getById(fileCopy.getNaturalId().backupTargetId());
            StorageSolution storageSolution = storageSolutionRepository.getById(backupTarget.getStorageSolutionId());
            markInProgress(fileCopy);
            String filePath = uniqueFilePathResolver.resolve(backupTarget.getPathTemplate(), gameFile.getFileSource(),
                    storageSolution);
            updateFilePath(fileCopy, filePath);
            tryToDownloadToDisk(storageSolution, gameFile, fileCopy, filePath);
            markDownloaded(fileCopy, filePath);
        } catch (IOException | RuntimeException e) {
            markFailed(fileCopy, e);
            throw new FileBackupFailedException(gameFile, fileCopy, e);
        }
    }

    private void tryToDownloadToDisk(StorageSolution storageSolution,
                                     GameFile gameFile, FileCopy fileCopy, String filePath) throws IOException {
        try {
            downloadToDisk(storageSolution, gameFile, fileCopy);
        } catch (IOException e) {
            tryToCleanUpAfterFailedDownload(storageSolution, fileCopy, filePath);
            throw e;
        }
    }

    private void downloadToDisk(StorageSolution storageSolution, GameFile gameFile, FileCopy fileCopy
    ) throws IOException {
        GameProviderId gameProviderId = gameFile.getFileSource().gameProviderId();
        GameProviderFileBackupService gameProviderFileBackupService = getGameProviderFileBackupService(gameProviderId);
        DownloadProgress downloadProgress = downloadProgressFactory.create();

        gameProviderFileBackupService.backUpFile(storageSolution, gameFile, fileCopy, downloadProgress);
    }

    private GameProviderFileBackupService getGameProviderFileBackupService(GameProviderId gameProviderId) {
        if (!gameProviderFileBackupServices.containsKey(gameProviderId)) {
            throw new IllegalArgumentException("File backup service for gameProviderId not found: " + gameProviderId);
        }

        return gameProviderFileBackupServices.get(gameProviderId);
    }

    private void tryToCleanUpAfterFailedDownload(StorageSolution storageSolution, FileCopy fileCopy, String filePath) {
        storageSolution.deleteIfExists(filePath);
        fileCopy.setFilePath(null);
        fileCopyRepository.save(fileCopy);
    }

    private void markInProgress(FileCopy fileCopy) {
        fileCopy.toInProgress();
        fileCopyRepository.save(fileCopy);
    }

    private void updateFilePath(FileCopy fileCopy, String filePath) {
        fileCopy.setFilePath(filePath);
        fileCopyRepository.save(fileCopy);
    }

    private void markDownloaded(FileCopy fileCopy, String downloadedPath) {
        fileCopy.toSuccessful(downloadedPath);
        fileCopyRepository.save(fileCopy);
    }

    private void markFailed(FileCopy fileCopy, Exception exception) {
        String message = exception.getMessage();
        if (message == null) {
            message = "Unknown error";
        }
        fileCopy.toFailed(message);
        fileCopyRepository.save(fileCopy);
    }

    public boolean isReadyFor(GameFile gameFile) {
        return getGameProviderFileBackupService(gameFile.getFileSource().gameProviderId()).isReady();
    }
}
