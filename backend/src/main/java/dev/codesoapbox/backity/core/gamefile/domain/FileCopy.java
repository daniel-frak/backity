package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.gamefile.domain.exceptions.FilePathMustNotBeNullForSuccessfulFileCopy;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

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
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class FileCopy {

    @EqualsAndHashCode.Include
    private final FileCopyId id;

    private @NonNull FileBackupStatus status;
    private String failedReason;

    private String filePath;

    public FileCopy(@NonNull FileCopyId id, @NonNull FileBackupStatus status,
                    String failedReason, String filePath) {
        this.id = id;
        this.status = status;
        this.failedReason = failedReason;
        this.filePath = filePath;

        validateIntegrity();
    }

    private void validateIntegrity() {
        if (this.status == FileBackupStatus.FAILED && this.failedReason == null) {
            throw new IllegalArgumentException("failedReason is required");
        }
        if (this.status == FileBackupStatus.SUCCESS && this.filePath == null) {
            throw new IllegalArgumentException("filePath is required");
        }
        if (this.status != FileBackupStatus.FAILED && this.failedReason != null) {
            throw new IllegalArgumentException("failedReason must be null for this status");
        }
    }

    public void toDiscovered() {
        this.status = FileBackupStatus.DISCOVERED;
        this.failedReason = null;
    }

    public void toEnqueued() {
        this.status = FileBackupStatus.ENQUEUED;
        this.failedReason = null;
    }

    public void toInProgress() {
        this.status = FileBackupStatus.IN_PROGRESS;
        this.failedReason = null;
    }

    public void toSuccessful(@NonNull String filePath) {
        this.status = FileBackupStatus.SUCCESS;
        this.failedReason = null;
        this.filePath = filePath;
    }

    public void toFailed(@NonNull String failedReason) {
        this.status = FileBackupStatus.FAILED;
        this.failedReason = failedReason;
    }

    public void setFilePath(String filePath) {
        if(filePath == null && status == FileBackupStatus.SUCCESS) {
            throw new FilePathMustNotBeNullForSuccessfulFileCopy(id);
        }
        this.filePath = filePath;
    }
}
