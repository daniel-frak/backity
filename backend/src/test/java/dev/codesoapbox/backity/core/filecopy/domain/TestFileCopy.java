package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import lombok.Builder;

import java.time.LocalDateTime;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild",
        builderMethodName = "discoveredBuilder")
public class TestFileCopy {

    @lombok.Builder.Default
    private FileCopyId id = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");

    @lombok.Builder.Default
    private GameFileId gameFileId = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");

    @lombok.Builder.Default
    private BackupTargetId backupTargetId = new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76");

    @lombok.Builder.Default
    private FileBackupStatus status = FileBackupStatus.DISCOVERED;

    @lombok.Builder.Default
    private String failedReason = null;

    @lombok.Builder.Default
    private String filePath = null;

    @lombok.Builder.Default
    private LocalDateTime dateCreated = LocalDateTime.parse("2022-04-29T14:15:53");

    @lombok.Builder.Default
    private LocalDateTime dateModified = LocalDateTime.parse("2023-04-29T14:15:53");

    public static FileCopy discovered() {
        return discoveredBuilder().build();
    }

    public static FileCopy successful() {
        return successfulBuilder()
                .build();
    }

    public static Builder successfulBuilder() {
        return discoveredBuilder()
                .status(FileBackupStatus.SUCCESS)
                .filePath("someFilePath");
    }

    public static FileCopy enqueued() {
        return enqueuedBuilder()
                .build();
    }

    public static Builder enqueuedBuilder() {
        return discoveredBuilder()
                .status(FileBackupStatus.ENQUEUED);
    }

    public static FileCopy failed() {
        return failedBuilder()
                .build();
    }

    public static Builder failedBuilder() {
        return discoveredBuilder()
                .status(FileBackupStatus.FAILED)
                .failedReason("someFailedReason");
    }

    public static FileCopy inProgress() {
        return inProgressBuilder()
                .build();
    }

    public static Builder inProgressBuilder() {
        return discoveredBuilder()
                .status(FileBackupStatus.IN_PROGRESS)
                .filePath("someFilePath");
    }

    public static class Builder {

        public FileCopy build() {
            var temp = internalBuild();
            return new FileCopy(
                    temp.id,
                    temp.gameFileId,
                    temp.backupTargetId,
                    temp.status,
                    temp.failedReason,
                    temp.filePath,
                    temp.dateCreated,
                    temp.dateModified
            );
        }
    }
}