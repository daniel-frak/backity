package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgress;
import dev.codesoapbox.backity.core.backup.application.downloadprogress.DownloadProgressFactory;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FileCopyReplicator {

    private final Map<GameProviderId, GameProviderFileBackupService> gameProviderFileBackupServices;
    private final DownloadProgressFactory downloadProgressFactory;

    public FileCopyReplicator(List<GameProviderFileBackupService> gameProviderFileBackupServices,
                              DownloadProgressFactory downloadProgressFactory) {
        this.gameProviderFileBackupServices = gameProviderFileBackupServices.stream()
                .collect(Collectors.toMap(GameProviderFileBackupService::getGameProviderId,
                        d -> d));
        this.downloadProgressFactory = downloadProgressFactory;
    }

    public void replicateFile(StorageSolution storageSolution, GameFile gameFile, FileCopy fileCopy)
            throws IOException {
        if (fileCopy.getStatus() != FileCopyStatus.IN_PROGRESS) {
            throw new IllegalArgumentException(
                    "Cannot replicate file copy that is not in progress (id=" + fileCopy.getId() + ")");
        }

        GameProviderId gameProviderId = gameFile.getFileSource().gameProviderId();
        GameProviderFileBackupService gameProviderFileBackupService = getGameProviderFileBackupService(gameProviderId);
        DownloadProgress downloadProgress = downloadProgressFactory.create();

        gameProviderFileBackupService.replicateFile(storageSolution, gameFile, fileCopy, downloadProgress);
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
