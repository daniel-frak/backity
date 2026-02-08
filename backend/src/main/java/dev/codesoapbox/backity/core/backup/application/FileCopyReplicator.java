package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTracker;
import dev.codesoapbox.backity.core.backup.application.writeprogress.OutputStreamProgressTrackerFactory;
import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class FileCopyReplicator {

    private final Map<GameProviderId, GameProviderFileBackupService> gameProviderFileBackupServices;
    private final OutputStreamProgressTrackerFactory outputStreamProgressTrackerFactory;
    private final StorageSolutionWriteService storageSolutionWriteService;

    public FileCopyReplicator(List<GameProviderFileBackupService> gameProviderFileBackupServices,
                              OutputStreamProgressTrackerFactory outputStreamProgressTrackerFactory,
                              StorageSolutionWriteService storageSolutionWriteService) {
        this.gameProviderFileBackupServices = gameProviderFileBackupServices.stream()
                .collect(Collectors.toMap(GameProviderFileBackupService::getGameProviderId,
                        d -> d));
        this.outputStreamProgressTrackerFactory = outputStreamProgressTrackerFactory;
        this.storageSolutionWriteService = storageSolutionWriteService;
    }

    public void replicate(StorageSolution storageSolution, SourceFile sourceFile, FileCopy fileCopy) {
        if (fileCopy.getStatus() != FileCopyStatus.IN_PROGRESS) {
            throw new IllegalArgumentException(
                    "Cannot replicate file copy that is not in progress (id=" + fileCopy.getId() + ")");
        }

        GameProviderId gameProviderId = sourceFile.getGameProviderId();
        GameProviderFileBackupService gameProviderFileBackupService = getGameProviderFileBackupService(gameProviderId);
        OutputStreamProgressTracker outputStreamProgressTracker = outputStreamProgressTrackerFactory.create(fileCopy);
        TrackableFileStream fileStream = gameProviderFileBackupService.acquireTrackableFileStream(
                sourceFile, outputStreamProgressTracker);
        String filePath = fileCopy.getFilePath();
        storageSolutionWriteService.writeFileToStorage(fileStream, storageSolution, filePath);
        log.info("Replicated file {} to {}", sourceFile, filePath);
    }

    private GameProviderFileBackupService getGameProviderFileBackupService(GameProviderId gameProviderId) {
        if (!gameProviderFileBackupServices.containsKey(gameProviderId)) {
            throw new IllegalArgumentException("File backup service for gameProviderId not found: " + gameProviderId);
        }

        return gameProviderFileBackupServices.get(gameProviderId);
    }

    public boolean gameProviderIsConnected(SourceFile sourceFile) {
        return getGameProviderFileBackupService(sourceFile.getGameProviderId()).isConnected();
    }
}
