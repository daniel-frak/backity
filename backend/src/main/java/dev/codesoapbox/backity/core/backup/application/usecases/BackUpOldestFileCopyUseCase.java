package dev.codesoapbox.backity.core.backup.application.usecases;

import dev.codesoapbox.backity.core.backup.application.FileBackupService;
import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RequiredArgsConstructor
public class BackUpOldestFileCopyUseCase implements DomainEventHandler<BackupRecoveryCompletedEvent> {

    final AtomicReference<FileCopy> enqueuedFileCopyReference = new AtomicReference<>();
    private final FileCopyRepository fileCopyRepository;
    private final GameFileRepository gameFileRepository;
    private final BackupTargetRepository backupTargetRepository;
    private final StorageSolutionRepository storageSolutionRepository;
    private final FileBackupService fileBackupService;
    private final AtomicBoolean recoveryCompleted = new AtomicBoolean(false);

    public synchronized void backUpOldestFileCopy() {
        if (enqueuedFileCopyReference.get() != null) {
            return;
        }
        if (!recoveryCompleted.get()) {
            /*
            Otherwise RecoverInterruptedFileBackupUseCase could accidentally try to recover a File Copy that
            has been correctly moved to "in progress" here (as we can't guarantee method call order).
             */
            return;
        }

        fileCopyRepository.findOldestEnqueued()
                .ifPresent(this::tryToBackUp);
    }

    private void tryToBackUp(FileCopy fileCopy) {
        enqueuedFileCopyReference.set(fileCopy);

        try {
            backUp(fileCopy);
        } catch (RuntimeException e) {
            log.error("An error occurred while trying to process enqueued game file (id: {})",
                    fileCopy.getId(), e);
        } finally {
            enqueuedFileCopyReference.set(null);
        }
    }

    /*
    Not that the current implementation won't mark the FileCopy as "FAILED" when one of the repository calls fails,
    potentially leading to backups getting stuck, as the failing backup will always be the first in the queue.
    This is probably not a problem, though, as these repository calls failing is likely to be a problem unrelated
    to the FileCopy itself.
     */
    private void backUp(FileCopy fileCopy) {
        GameFile gameFile = gameFileRepository.getById(fileCopy.getNaturalId().gameFileId());
        BackupTarget backupTarget = backupTargetRepository.getById(fileCopy.getNaturalId().backupTargetId());
        StorageSolution storageSolution = storageSolutionRepository.getById(backupTarget.getStorageSolutionId());

        fileBackupService.backUpFile(fileCopy, gameFile, backupTarget, storageSolution);
    }

    public Class<BackupRecoveryCompletedEvent> getEventClass() {
        return BackupRecoveryCompletedEvent.class;
    }

    @Override
    public void handle(BackupRecoveryCompletedEvent event) {
        recoveryCompleted.set(true);
    }
}
