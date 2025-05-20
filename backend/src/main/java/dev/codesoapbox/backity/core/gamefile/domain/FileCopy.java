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
public record FileCopy(
        @NonNull FileBackupStatus status,
        String failedReason,
        @With String filePath
) {

    public FileCopy {
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

    public FileCopy toDiscovered() {
        return new FileCopy(FileBackupStatus.DISCOVERED, null, filePath);
    }

    public FileCopy toEnqueued() {
        return new FileCopy(FileBackupStatus.ENQUEUED, null, filePath);
    }

    public FileCopy toInProgress() {
        return new FileCopy(FileBackupStatus.IN_PROGRESS, null, filePath);
    }

    public FileCopy toSuccessful(String filePath) {
        return new FileCopy(FileBackupStatus.SUCCESS, null, filePath);
    }

    public FileCopy toFailed(String failedReason) {
        return new FileCopy(FileBackupStatus.FAILED, failedReason, filePath);
    }
}
