package dev.codesoapbox.backity.core.backup.domain;

import java.util.concurrent.atomic.AtomicBoolean;

/// Coordinates file copy replication to ensure it starts only when the application is ready
/// and that only one replication process runs at a time.
///
/// # Concurrency warning
///
/// Process information is maintained in memory. Therefore, file copy replication cannot be coordinated
/// across multiple instances of this class or multiple instances of the application.
public class FileCopyReplicationProcess {

    /*
    If we don't check this, RecoverInterruptedFileBackupUseCase could accidentally try to recover a File Copy that
    has been correctly moved to "in progress" here (as we can't guarantee method call order).
     */
    private final AtomicBoolean backupRecoveryCompleted = new AtomicBoolean(false);

    private final AtomicBoolean isInProgress = new AtomicBoolean(false);

    public void markAsCompleted() {
        isInProgress.set(false);
    }

    public void markBackupRecoveryCompleted() {
        backupRecoveryCompleted.set(true);
    }

    public boolean tryStart() {
        return backupRecoveryCompleted.get()
                && isInProgress.compareAndSet(false, true);
    }
}
