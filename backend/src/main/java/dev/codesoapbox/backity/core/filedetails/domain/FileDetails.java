package dev.codesoapbox.backity.core.filedetails.domain;

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
public class FileDetails {

    @NonNull
    @EqualsAndHashCode.Include
    private FileDetailsId id;

    @NonNull
    private GameId gameId;

    @NonNull
    private SourceFileDetails sourceFileDetails;

    @NonNull
    private BackupDetails backupDetails;

    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    public void enqueue() {
        backupDetails.setStatus(FileBackupStatus.ENQUEUED);
    }

    public void fail(String failedReason) {
        backupDetails.setStatus(FileBackupStatus.FAILED);
        backupDetails.setFailedReason(failedReason);
    }

    public void markAsInProgress() {
        backupDetails.setStatus(FileBackupStatus.IN_PROGRESS);
    }

    public void markAsDownloaded(String filePath) {
        backupDetails.setFilePath(filePath);
        backupDetails.setStatus(FileBackupStatus.SUCCESS);
    }

    public void updateFilePath(String filePath) {
        backupDetails.setFilePath(filePath);
    }

    public void clearFilePath() {
        backupDetails.setFilePath(null);
    }
}
