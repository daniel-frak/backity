package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotBackedUpException;
import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameProviderFileUrlEmptyException;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import lombok.*;
import org.apache.logging.log4j.util.Strings;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private GameProviderFile gameProviderFile;

    @NonNull
    private FileBackup fileBackup;

    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    @Getter
    @NonNull
    private List<DomainEvent> domainEvents;

    public static GameFile associate(Game game, GameProviderFile gameProviderFile) {
        return new GameFile(
                GameFileId.newInstance(),
                game.getId(),
                gameProviderFile,
                new FileBackup(FileBackupStatus.DISCOVERED, null, null),
                null,
                null,
                new ArrayList<>()
        );
    }

    public void enqueue() {
        fileBackup.setStatus(FileBackupStatus.ENQUEUED);
    }

    public void fail(String failedReason) {
        fileBackup.setStatus(FileBackupStatus.FAILED);
        fileBackup.setFailedReason(failedReason);

        var event = new FileBackupFailedEvent(id, failedReason);
        domainEvents.add(event);
    }

    public void markAsInProgress() {
        fileBackup.setStatus(FileBackupStatus.IN_PROGRESS);

        var fileBackupStartedEvent = new FileBackupStartedEvent(
                id,
                gameProviderFile.originalGameTitle(),
                gameProviderFile.fileTitle(),
                gameProviderFile.version(),
                gameProviderFile.originalFileName(),
                gameProviderFile.size(),
                fileBackup.getFilePath()
        );
        domainEvents.add(fileBackupStartedEvent);
    }

    public void markAsDownloaded(String filePath) {
        fileBackup.setFilePath(filePath);
        fileBackup.setStatus(FileBackupStatus.SUCCESS);

        var event = new FileBackupFinishedEvent(id);
        domainEvents.add(event);
    }

    public void updateFilePath(String filePath) {
        fileBackup.setFilePath(filePath);
    }

    public void clearFilePath() {
        fileBackup.setFilePath(null);
    }

    public void validateIsBackedUp() {
        if (fileBackup.getStatus() != FileBackupStatus.SUCCESS) {
            throw new GameFileNotBackedUpException(id);
        }
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }

    public void validateReadyForDownload() {
        if (Strings.isBlank(gameProviderFile.url())) {
            throw new GameProviderFileUrlEmptyException(id);
        }
    }
}
