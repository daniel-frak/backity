package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotBackedUpException;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * A version of a game file, either not yet downloaded, already downloaded or anything in-between.
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class GameFile {

    @NonNull
    @EqualsAndHashCode.Include
    private GameFileId id;

    @NonNull
    private GameId gameId;

    @NonNull
    private FileSource fileSource;

    @NonNull
    private FileCopy fileCopy;

    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    @NonNull
    private List<DomainEvent> domainEvents;

    public static GameFile createFor(Game game, FileSource fileSource) {
        return new GameFile(
                GameFileId.newInstance(),
                game.getId(),
                fileSource,
                new FileCopy(FileBackupStatus.DISCOVERED, null, null),
                null,
                null,
                new ArrayList<>()
        );
    }

    public void markAsDiscovered() {
        fileCopy = fileCopy.toDiscovered();
    }

    public void markAsEnqueued() {
        fileCopy = fileCopy.toEnqueued();
    }

    public void markAsFailed(String failedReason) {
        fileCopy = fileCopy.toFailed(failedReason);

        var event = new FileBackupFailedEvent(id, failedReason);
        domainEvents.add(event);
    }

    public void markAsInProgress() {
        fileCopy = fileCopy.toInProgress();

        var fileBackupStartedEvent = new FileBackupStartedEvent(
                id,
                fileSource.originalGameTitle(),
                fileSource.fileTitle(),
                fileSource.version(),
                fileSource.originalFileName(),
                fileSource.size(),
                fileCopy.filePath()
        );
        domainEvents.add(fileBackupStartedEvent);
    }

    public void markAsDownloaded(String filePath) {
        fileCopy = fileCopy.toSuccessful(filePath);

        var event = new FileBackupFinishedEvent(id);
        domainEvents.add(event);
    }

    public void updateFilePath(String filePath) {
        fileCopy = fileCopy.withFilePath(filePath);
    }

    public void clearFilePath() {
        fileCopy = fileCopy.withFilePath(null);
    }

    public void validateIsBackedUp() {
        if (fileCopy.status() != FileBackupStatus.SUCCESS) {
            throw new GameFileNotBackedUpException(id);
        }
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    public List<DomainEvent> getDomainEvents() {
        return unmodifiableList(this.domainEvents);
    }
}
