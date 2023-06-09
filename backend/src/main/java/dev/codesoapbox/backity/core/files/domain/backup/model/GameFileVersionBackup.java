package dev.codesoapbox.backity.core.files.domain.backup.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * A version of a game file, either not yet downloaded, already downloaded or anything in-between.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameFileVersionBackup {

    @NotNull
    private Long id;

    @NotNull
    private String source;

    @NotNull
    private String url;

    @NotNull
    private String title;

    @NotNull
    private String originalFileName;

    private String filePath;

    @NotNull
    private String gameTitle;

    @NotNull String gameId;

    @NotNull
    private String version;

    @NotNull
    private String size;

    @NotNull
    private LocalDateTime dateCreated;

    @NotNull
    private LocalDateTime dateModified;

    @NotNull
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
