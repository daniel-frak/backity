package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgressFactory;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileCopyReplicator {

    private final Map<GameProviderId, GameProviderFileBackupService> gameProviderFileBackupServices;
    private final DownloadProgressFactory downloadProgressFactory;
    private final DownloadService downloadService;

    public FileCopyReplicator(List<GameProviderFileBackupService> gameProviderFileBackupServices,
                              DownloadProgressFactory downloadProgressFactory, DownloadService downloadService) {
        this.gameProviderFileBackupServices = gameProviderFileBackupServices.stream()
                .collect(Collectors.toMap(GameProviderFileBackupService::getGameProviderId,
                        d -> d));
        this.downloadProgressFactory = downloadProgressFactory;
        this.downloadService = downloadService;
    }

    public void replicate(StorageSolution storageSolution, GameFile gameFile, FileCopy fileCopy) {
        if (fileCopy.getStatus() != FileCopyStatus.IN_PROGRESS) {
            throw new IllegalArgumentException(
                    "Cannot replicate file copy that is not in progress (id=" + fileCopy.getId() + ")");
        }

        GameProviderId gameProviderId = gameFile.getFileSource().gameProviderId();
        GameProviderFileBackupService gameProviderFileBackupService = getGameProviderFileBackupService(gameProviderId);
        DownloadProgress downloadProgress = downloadProgressFactory.create(fileCopy);
        TrackableFileStream fileStream = gameProviderFileBackupService.acquireTrackableFileStream(
                gameFile, downloadProgress);
        String filePath = fileCopy.getFilePath();
        downloadService.downloadFile(storageSolution, fileStream, gameFile, filePath);
    }

    private GameProviderFileBackupService getGameProviderFileBackupService(GameProviderId gameProviderId) {
        if (!gameProviderFileBackupServices.containsKey(gameProviderId)) {
            throw new IllegalArgumentException("File backup service for gameProviderId not found: " + gameProviderId);
        }

        return gameProviderFileBackupServices.get(gameProviderId);
    }

    public boolean gameProviderIsConnected(GameFile gameFile) {
        return getGameProviderFileBackupService(gameFile.getFileSource().gameProviderId()).isConnected();
    }
}
