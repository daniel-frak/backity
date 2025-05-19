package dev.codesoapbox.backity.core.gamefile.domain;

import lombok.NonNull;
import lombok.With;

/**
 * Represents a backup copy of a file in a Backup Target.
 * <p>
 * The file path must be updated to a temporary file after the status changes to 'in progress',
 * but before being marked as successful.
 * The file path should not be removed during status transitions,
 * as this could lead to accidentally losing track of the physical file.
 * The physical file resource must be deleted before its path is removed.
 * Use the `withFilePath` method to manage both updates and removals.
 */
public record FileBackup(
        @NonNull FileBackupStatus status,
        String failedReason,
        @With String filePath
) {

    public FileBackup {
        if (status == FileBackupStatus.FAILED && failedReason == null) {
            throw new IllegalArgumentException("failedReason is required");
        }
        if (status == FileBackupStatus.SUCCESS && filePath == null) {
            throw new IllegalArgumentException("filePath is required");
        }
        if (status != FileBackupStatus.FAILED && failedReason != null) {
            throw new IllegalArgumentException("failedReason must be null for this status");
        }

    }

    public FileBackup toDiscovered() {
        return new FileBackup(FileBackupStatus.DISCOVERED, null, filePath);
    }

    public FileBackup toEnqueued() {
        return new FileBackup(FileBackupStatus.ENQUEUED, null, filePath);
    }

    public FileBackup toInProgress() {
        return new FileBackup(FileBackupStatus.IN_PROGRESS, null, filePath);
    }

    public FileBackup toSuccessful(String filePath) {
        return new FileBackup(FileBackupStatus.SUCCESS, null, filePath);
    }

    public FileBackup toFailed(String failedReason) {
        return new FileBackup(FileBackupStatus.FAILED, failedReason, filePath);
    }
}
