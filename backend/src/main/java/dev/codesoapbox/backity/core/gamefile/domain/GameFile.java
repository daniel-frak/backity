package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.game.domain.GameId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.time.LocalDateTime;

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

    public void enqueue() {
        fileBackup.setStatus(FileBackupStatus.ENQUEUED);
    }

    public void fail(String failedReason) {
        fileBackup.setStatus(FileBackupStatus.FAILED);
        fileBackup.setFailedReason(failedReason);
    }

    public void markAsInProgress() {
        fileBackup.setStatus(FileBackupStatus.IN_PROGRESS);
    }

    public void markAsDownloaded(String filePath) {
        fileBackup.setFilePath(filePath);
        fileBackup.setStatus(FileBackupStatus.SUCCESS);
    }

    public void updateFilePath(String filePath) {
        fileBackup.setFilePath(filePath);
    }

    public void clearFilePath() {
        fileBackup.setFilePath(null);
    }
}
