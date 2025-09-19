package dev.codesoapbox.backity.core.backup.domain;

import java.util.concurrent.atomic.AtomicBoolean;

public class FileCopyReplicationProcess {

    /*
    If we don't check this, RecoverInterruptedFileBackupUseCase could accidentally try to recover a File Copy that
    has been correctly moved to "in progress" here (as we can't guarantee method call order).
     */
    private final AtomicBoolean backupRecoveryCompleted = new AtomicBoolean(false);

    private final AtomicBoolean isInProgress = new AtomicBoolean(false);

    public void markAsInProgress() {
        isInProgress.set(true);
    }

    public void markAsCompleted() {
        isInProgress.set(false);
    }

    public void markBackupRecoveryCompleted() {
        backupRecoveryCompleted.set(true);
    }

    public boolean canStart() {
        return !isInProgress.get() && backupRecoveryCompleted.get();
    }
}
