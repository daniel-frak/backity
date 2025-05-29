package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;

import java.time.LocalDateTime;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild",
        builderMethodName = "trackedBuilder")
public class TestFileCopy {

    @lombok.Builder.Default
    private FileCopyId id = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");

    @lombok.Builder.Default
    private FileCopyNaturalId naturalId = new FileCopyNaturalId(
            new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48"),
            new BackupTargetId("eda52c13-ddf7-406f-97d9-d3ce2cab5a76")
    );

    @lombok.Builder.Default
    private FileCopyStatus status = FileCopyStatus.TRACKED;

    @lombok.Builder.Default
    private String failedReason = null;

    @lombok.Builder.Default
    private String filePath = null;

    @lombok.Builder.Default
    private LocalDateTime dateCreated = LocalDateTime.parse("2022-04-29T14:15:53");

    @lombok.Builder.Default
    private LocalDateTime dateModified = LocalDateTime.parse("2023-04-29T14:15:53");

    public static FileCopy tracked() {
        return trackedBuilder().build();
    }

    public static FileCopy storedIntegrityVerified() {
        return storedIntegrityVerifiedBuilder()
                .build();
    }

    public static Builder storedIntegrityVerifiedBuilder() {
        return storedIntegrityUnknownBuilder()
                .status(FileCopyStatus.STORED_INTEGRITY_VERIFIED);
    }

    public static FileCopy storedIntegrityUnknown() {
        return storedIntegrityUnknownBuilder()
                .build();
    }

    public static Builder storedIntegrityUnknownBuilder() {
        return trackedBuilder()
                .status(FileCopyStatus.STORED_INTEGRITY_UNKNOWN)
                .filePath("someFilePath");
    }

    public static FileCopy enqueued() {
        return enqueuedBuilder()
                .build();
    }

    public static Builder enqueuedBuilder() {
        return trackedBuilder()
                .status(FileCopyStatus.ENQUEUED);
    }

    public static FileCopy failed() {
        return failedBuilder()
                .build();
    }

    public static Builder failedBuilder() {
        return trackedBuilder()
                .status(FileCopyStatus.FAILED)
                .failedReason("someFailedReason");
    }

    public static FileCopy inProgressWithoutFilePath() {
        return inProgressWithoutFilePathBuilder()
                .build();
    }

    public static Builder inProgressWithoutFilePathBuilder() {
        return trackedBuilder()
                .status(FileCopyStatus.IN_PROGRESS);
    }

    public static FileCopy inProgressWithFilePath() {
        return inProgressWithFilePathBuilder();
    }

    private static FileCopy inProgressWithFilePathBuilder() {
        return inProgressWithoutFilePathBuilder()
                .filePath("someFilePath")
                .build();
    }

    public static class Builder {

        public FileCopy build() {
            var temp = internalBuild();
            return new FileCopy(
                    temp.id,
                    temp.naturalId,
                    temp.status,
                    temp.failedReason,
                    temp.filePath,
                    temp.dateCreated,
                    temp.dateModified
            );
        }
    }
}