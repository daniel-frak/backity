package dev.codesoapbox.backity.core.gamefile.domain;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild",
        builderMethodName = "discoveredBuilder")
public class TestFileBackup {

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
        return discoveredBuilder()
                .status(FileBackupStatus.SUCCESS)
                .filePath("someFilePath")
                .build();
    }

    public static FileCopy enqueued() {
        return discoveredBuilder()
                .status(FileBackupStatus.ENQUEUED)
                .build();
    }

    public static FileCopy failed() {
        return discoveredBuilder()
                .status(FileBackupStatus.FAILED)
                .failedReason("someFailedReason")
                .build();
    }

    public static FileCopy inProgress() {
        return discoveredBuilder()
                .status(FileBackupStatus.IN_PROGRESS)
                .filePath("someFilePath")
                .build();
    }

    public static class Builder {

        public FileCopy build() {
            var temp = internalBuild();
            return new FileCopy(
                    temp.status,
                    temp.failedReason,
                    temp.filePath
            );
        }
    }
}