package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backup.application.exceptions.FileWriteWasCanceledException;
import dev.codesoapbox.backity.core.backup.domain.exceptions.FileBackupFailedException;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyFailureReason;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePathReservation;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Orchestrates the overall backup workflow, including state management and cleanup operations.
 */
@Slf4j
@RequiredArgsConstructor
public class FileBackupService {

    private final UniqueFilePathResolver uniqueFilePathResolver;
    private final FileCopyRepository fileCopyRepository;
    private final FileCopyReplicator fileCopyReplicator;

    public void backUpFile(FileBackupContext fileBackupContext) {
        SourceFile sourceFile = fileBackupContext.sourceFile();
        if (!fileCopyReplicator.gameProviderIsConnected(sourceFile)) {
            log.debug("Game provider for source file (id={}) is not connected, skipping...", sourceFile.getId());
            return;
        }
        FileCopy fileCopy = fileBackupContext.fileCopy();
        log.info("Backing up source file (id={}, fileCopyId={}, url={})...", sourceFile.getId(),
                fileCopy.getId(), sourceFile.getUrl());

        StorageSolution storageSolution = fileBackupContext.storageSolution();
        try {
            BackupTarget backupTarget = fileBackupContext.backupTarget();
            tryToBackUp(fileCopy, sourceFile, backupTarget, storageSolution);
        } catch (RuntimeException e) {
            try {
                deleteFileAndMarkFailed(fileCopy, storageSolution, e);
            } catch (RuntimeException e1) {
                markFailedWithoutDeletingFile(fileCopy, e, e1);
            }
            throw new FileBackupFailedException(sourceFile, fileCopy, e);
        }
    }

    @SuppressWarnings("java:S1166") // Intentionally suppressing FileWriteWasCanceledException
    private void tryToBackUp(FileCopy fileCopy, SourceFile sourceFile, BackupTarget backupTarget,
                             StorageSolution storageSolution) {
        /*
        Note that the file path is only guaranteed to be unique within this replication process.
        If the replication fails, a new file path MUST be resolved, as the UniqueFilePathResolver
        does not verify against persisted file paths stored in the database.
         */
        try (FilePathReservation filePathReservation = uniqueFilePathResolver.resolve(
                backupTarget.getPathTemplate(), sourceFile, storageSolution)) {
            markInProgress(fileCopy, filePathReservation.get());
            fileCopyReplicator.replicate(storageSolution, sourceFile, fileCopy);
        } catch (FileWriteWasCanceledException _) {
            storageSolution.deleteIfExists(fileCopy.getFilePath());
            markCanceled(fileCopy);
            return;
        }
        markStored(fileCopy);
    }

    public void markInProgress(FileCopy fileCopy, FilePath filePath) {
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

    public void markFailed(FileCopy fileCopy, FilePath filePath, Exception exception) {
        String message = exception.getMessage();
        if (message == null) {
            message = "Unknown error";
        }
        var failedReason = new FileCopyFailureReason(message);

        fileCopy.toFailed(failedReason, filePath);
        fileCopyRepository.save(fileCopy);
    }

    private void markFailedWithoutDeletingFile(FileCopy fileCopy, Exception e, RuntimeException e1) {
        log.error("An error occurred while trying to delete file (id={}, filePath={})", fileCopy.getId(),
                fileCopy.getFilePath(), e1);
        markFailed(fileCopy, fileCopy.getFilePath(), e);
    }
}
