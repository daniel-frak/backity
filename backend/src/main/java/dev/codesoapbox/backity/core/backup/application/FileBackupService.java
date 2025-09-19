package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.exceptions.FileDownloadWasCanceledException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * Wrapper for all gameProviderId file backup services.
 * <p>
 * Downloads files from remote servers.
 */
@Slf4j
@RequiredArgsConstructor
public class FileBackupService {

    private final UniqueFilePathResolver uniqueFilePathResolver;
    private final FileCopyRepository fileCopyRepository;
    private final FileCopyReplicator fileCopyReplicator;

    public void backUpFile(FileBackupContext fileBackupContext) {
        FileCopy fileCopy = fileBackupContext.fileCopy();
        GameFile gameFile = fileBackupContext.gameFile();
        BackupTarget backupTarget = fileBackupContext.backupTarget();
        StorageSolution storageSolution = fileBackupContext.storageSolution();

        if (!fileCopyReplicator.gameProviderIsConnected(gameFile)) {
            log.debug("Game provider for game file (id={}) is not connected, skipping...", gameFile.getId());
            return;
        }
        log.info("Backing up game file (id={}, fileCopyId={}, url={})...", gameFile.getId(),
                fileCopy.getId(), gameFile.getFileSource().url());

        try {
            tryToBackUp(fileCopy, gameFile, backupTarget, storageSolution);
        } catch (IOException | RuntimeException e) {
            try {
                deleteFileAndMarkFailed(fileCopy, storageSolution, e);
            } catch (RuntimeException e1) {
                markFailedWithoutDeletingFile(fileCopy, e, e1);
            }
            throw new FileBackupFailedException(gameFile, fileCopy, e);
        }
    }

    @SuppressWarnings("java:S1166") // Intentionally suppressing FileDownloadWasCanceledException
    private void tryToBackUp(FileCopy fileCopy, GameFile gameFile, BackupTarget backupTarget,
                             StorageSolution storageSolution) throws IOException {
        String filePath = uniqueFilePathResolver.resolve(
                backupTarget.getPathTemplate(), gameFile.getFileSource(), storageSolution);
        markInProgress(fileCopy, filePath);

        try {
            fileCopyReplicator.replicateFile(storageSolution, gameFile, fileCopy);
        } catch (FileDownloadWasCanceledException e) {
            storageSolution.deleteIfExists(fileCopy.getFilePath());
            markCanceled(fileCopy);
            return;
        }
        markStored(fileCopy);
    }

    public void markInProgress(FileCopy fileCopy, String filePath) {
        fileCopy.toInProgress(filePath);
        fileCopyRepository.save(fileCopy);
    }

    public void markStored(FileCopy fileCopy) {
        fileCopy.toStoredIntegrityUnknown();
        fileCopyRepository.save(fileCopy);
    }

    private void markCanceled(FileCopy fileCopy) {
        fileCopy.toCanceled();
        fileCopyRepository.save(fileCopy);
    }

    private void deleteFileAndMarkFailed(FileCopy fileCopy, StorageSolution storageSolution, Exception e) {
        if (fileCopy.getFilePath() != null) {
            storageSolution.deleteIfExists(fileCopy.getFilePath());
        }
        markFailed(fileCopy, null, e);
    }

    public void markFailed(FileCopy fileCopy, String filePath, Exception exception) {
        String message = exception.getMessage();
        if (message == null) {
            message = "Unknown error";
        }
        fileCopy.toFailed(message, filePath);
        fileCopyRepository.save(fileCopy);
    }

    private void markFailedWithoutDeletingFile(FileCopy fileCopy, Exception e, RuntimeException e1) {
        log.error("An error occurred while trying to delete file (id={}, filePath={})", fileCopy.getId(),
                fileCopy.getFilePath(), e1);
        markFailed(fileCopy, fileCopy.getFilePath(), e);
    }
}
