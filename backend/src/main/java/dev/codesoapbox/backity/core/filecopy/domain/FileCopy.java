package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FileCopyNotBackedUpException;
import dev.codesoapbox.backity.core.filecopy.domain.exceptions.FilePathMustNotBeNullForStoredFileCopy;
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
 * Represents a backup copy of a Game File in a Backup Target.
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

    private final FileCopyNaturalId naturalId;
    private final LocalDateTime dateCreated; // Provided by DB
    private final LocalDateTime dateModified; // Provided by DB
    private final List<DomainEvent> domainEvents;

    private FileCopyStatus status;
    private String failedReason;
    private String filePath;

    public FileCopy(@NonNull FileCopyId id, @NonNull FileCopyNaturalId naturalId,
                    @NonNull FileCopyStatus status, String failedReason, String filePath,
                    LocalDateTime dateCreated, LocalDateTime dateModified) {
        this.id = id;
        this.naturalId = naturalId;
        this.status = status;
        this.failedReason = failedReason;
        this.filePath = filePath;
        this.dateCreated = dateCreated;
        this.dateModified = dateModified;
        this.domainEvents = new ArrayList<>();

        validateIntegrity();
    }

    private void validateIntegrity() {
        if (this.status == FileCopyStatus.FAILED && this.failedReason == null) {
            throw new IllegalArgumentException("failedReason is required");
        }
        if (statusIsStored() && this.filePath == null) {
            throw new IllegalArgumentException("filePath is required");
        }
        if (this.status != FileCopyStatus.FAILED && this.failedReason != null) {
            throw new IllegalArgumentException("failedReason must be null for this status");
        }
    }

    private boolean statusIsStored() {
        return status == FileCopyStatus.STORED_INTEGRITY_UNKNOWN
               || status == FileCopyStatus.STORED_INTEGRITY_VERIFIED;
    }

    public void toTracked() {
        this.status = FileCopyStatus.TRACKED;
        this.failedReason = null;
    }

    public void toEnqueued() {
        this.status = FileCopyStatus.ENQUEUED;
        this.failedReason = null;
    }

    public void toInProgress() {
        this.status = FileCopyStatus.IN_PROGRESS;
        this.failedReason = null;
        var fileBackupStartedEvent = new FileBackupStartedEvent(id, naturalId, filePath);
        domainEvents.add(fileBackupStartedEvent);
    }

    public void toStoredIntegrityUnknown(@NonNull String filePath) {
        this.status = FileCopyStatus.STORED_INTEGRITY_UNKNOWN;
        this.failedReason = null;
        this.filePath = filePath;

        var event = new FileBackupFinishedEvent(id, naturalId);
        domainEvents.add(event);
    }

    public void toFailed(@NonNull String failedReason) {
        this.status = FileCopyStatus.FAILED;
        this.failedReason = failedReason;

        var event = new FileBackupFailedEvent(id, naturalId, failedReason);
        domainEvents.add(event);
    }

    public void setFilePath(String filePath) {
        if (filePath == null && statusIsStored()) {
            throw new FilePathMustNotBeNullForStoredFileCopy(id);
        }
        this.filePath = filePath;
    }

    public void validateIsBackedUp() {
        if (status != FileCopyStatus.STORED_INTEGRITY_UNKNOWN) {
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
