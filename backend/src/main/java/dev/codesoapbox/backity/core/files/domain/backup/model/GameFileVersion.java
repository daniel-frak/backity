package dev.codesoapbox.backity.core.files.domain.backup.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.time.LocalDateTime;

/**
 * A version of a game file, either not yet downloaded, already downloaded or anything in-between.
 */
@Data
@AllArgsConstructor
public class GameFileVersion {

    private Long id;

    @NonNull
    private String source;

    @NonNull
    private String url;

    @NonNull
    private String title;

    @NonNull
    private String originalFileName;

    private String filePath;
    private String gameTitle;

    @NonNull
    private String gameId;

    @NonNull
    private String version;

    @NonNull
    private String size;

    private LocalDateTime dateCreated;
    private LocalDateTime dateModified;

    @NonNull
    private FileBackupStatus backupStatus;

    private String backupFailedReason;

    public void enqueue() {
        this.backupStatus = FileBackupStatus.ENQUEUED;
    }

    public void fail(String failedReason) {
        this.backupStatus = FileBackupStatus.FAILED;
        this.backupFailedReason = failedReason;
    }

    public void markAsDownloaded(String filePath) {
        this.filePath = filePath;
        this.backupStatus = FileBackupStatus.SUCCESS;
    }
}
