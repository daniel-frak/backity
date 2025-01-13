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

    public static FileBackup discovered() {
        return discoveredBuilder().build();
    }

    public static FileBackup successful() {
        return discoveredBuilder()
                .status(FileBackupStatus.SUCCESS)
                .filePath("someFilePath")
                .build();
    }

    public static FileBackup enqueued() {
        return discoveredBuilder()
                .status(FileBackupStatus.ENQUEUED)
                .build();
    }

    public static FileBackup failed() {
        return discoveredBuilder()
                .status(FileBackupStatus.FAILED)
                .failedReason("someFailedReason")
                .build();
    }

    public static FileBackup inProgress() {
        return discoveredBuilder()
                .status(FileBackupStatus.IN_PROGRESS)
                .filePath("tempFilePath")
                .build();
    }

    public static class Builder {

        public FileBackup build() {
            var temp = internalBuild();
            return new FileBackup(
                    temp.status,
                    temp.failedReason,
                    temp.filePath
            );
        }
    }
}