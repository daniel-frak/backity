package dev.codesoapbox.backity.core.gamefile.domain;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild",
        builderMethodName = "discoveredBuilder")
public class TestFileCopy {

    @lombok.Builder.Default
    private FileCopyId id = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");

    @lombok.Builder.Default
    private FileBackupStatus status = FileBackupStatus.DISCOVERED;

    @lombok.Builder.Default
    private String failedReason = null;

    @lombok.Builder.Default
    private String filePath = null;

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
                    temp.status,
                    temp.failedReason,
                    temp.filePath
            );
        }
    }
}