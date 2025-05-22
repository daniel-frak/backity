package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotBackedUpException;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FilePathMustNotBeNullForSuccessfulFileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

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

    private final GameFileId gameFileId;
    private final BackupTargetId backupTargetId;

    private FileBackupStatus status;
    private String failedReason;
    private String filePath;

    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;
    private List<DomainEvent> domainEvents;

    public FileCopy(@NonNull FileCopyId id, @NonNull GameFileId gameFileId, @NonNull BackupTargetId backupTargetId,
                    @NonNull FileBackupStatus status, String failedReason, String filePath,
                    LocalDateTime dateCreated, LocalDateTime dateModified) {
        this.id = id;
        this.gameFileId = gameFileId;
        this.backupTargetId = backupTargetId;
        this.status = status;
        this.failedReason = failedReason;
        this.filePath = filePath;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.domainEvents = new ArrayList<>();

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
        var fileBackupStartedEvent = new FileBackupStartedEvent(id, gameFileId, filePath);
        domainEvents.add(fileBackupStartedEvent);
    }

    public void toSuccessful(@NonNull String filePath) {
        this.status = FileBackupStatus.SUCCESS;
        this.failedReason = null;
        this.filePath = filePath;

        var event = new FileBackupFinishedEvent(id, gameFileId);
        domainEvents.add(event);
    }

    public void toFailed(@NonNull String failedReason) {
        this.status = FileBackupStatus.FAILED;
        this.failedReason = failedReason;

        var event = new FileBackupFailedEvent(id, gameFileId, failedReason);
        domainEvents.add(event);
    }

    public void setFilePath(String filePath) {
        if (filePath == null && status == FileBackupStatus.SUCCESS) {
            throw new FilePathMustNotBeNullForSuccessfulFileCopy(id);
        }
        this.filePath = filePath;
    }

    public void validateIsBackedUp() {
        if (status != FileBackupStatus.SUCCESS) {
            throw new FileCopyNotBackedUpException(id);
        }
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    public List<DomainEvent> getDomainEvents() {
        return unmodifiableList(this.domainEvents);
    }
}
