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
public class GameFileVersionBackup {

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
    private FileBackupStatus status;

    private String failedReason;

    public void enqueue() {
        this.status = FileBackupStatus.ENQUEUED;
    }

    public void fail(String failedReason) {
        this.status = FileBackupStatus.FAILED;
        this.failedReason = failedReason;
    }

    public void markAsDownloaded(String filePath) {
        this.filePath = filePath;
        this.status = FileBackupStatus.SUCCESS;
    }
}
