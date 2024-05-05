package dev.codesoapbox.backity.core.filedetails.domain;

import dev.codesoapbox.backity.core.backup.domain.FileSourceId;
import dev.codesoapbox.backity.core.game.domain.GameId;

import java.time.LocalDateTime;
import java.util.UUID;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild",
        builderMethodName = "discoveredFileDetails")
public class TestFileDetails {

    @lombok.Builder.Default
    private FileDetailsId id = new FileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48"));

    @lombok.Builder.Default
    private GameId gameId = new GameId(UUID.fromString("1eec1c19-25bf-4094-b926-84b5bb8fa281"));

    @lombok.Builder.Default
    private FileSourceId sourceId = new FileSourceId("someSourceId");

    @lombok.Builder.Default
    private String originalGameTitle = "someOriginalGameTitle";

    @lombok.Builder.Default
    private String fileTitle = "someFileTitle";

    @lombok.Builder.Default
    private String version = "someVersion";

    @lombok.Builder.Default
    private String url = "someUrl";

    @lombok.Builder.Default
    private String originalFileName = "someOriginalFileName";

    @lombok.Builder.Default
    private String size = "5 KB";

    @lombok.Builder.Default
    private FileBackupStatus backupStatus = FileBackupStatus.DISCOVERED;

    @lombok.Builder.Default
    private String backupFailedReason = null;

    @lombok.Builder.Default
    private String filePath = null;

    @lombok.Builder.Default
    private LocalDateTime dateCreated = LocalDateTime.parse("2022-04-29T14:15:53");

    @lombok.Builder.Default
    private LocalDateTime dateModified = LocalDateTime.parse("2023-04-29T14:15:53");

    public static Builder discoveredFileDetails() {
        return new Builder();
    }

    public static Builder fullFileDetails() {
        return discoveredFileDetails()
                .backupFailedReason("someFailedReason")
                .filePath("someFilePath");
    }

    public static Builder successfulFileDetails() {
        return discoveredFileDetails()
                .backupStatus(FileBackupStatus.SUCCESS)
                .filePath("someFilePath");
    }

    public static Builder enqueuedFileDetails() {
        return discoveredFileDetails()
                .backupStatus(FileBackupStatus.ENQUEUED);
    }

    public static Builder failedFileDetails() {
        return discoveredFileDetails()
                .backupStatus(FileBackupStatus.FAILED)
                .backupFailedReason("someFailedReason");
    }

    public static Builder inProgressFileDetails() {
        return discoveredFileDetails()
                .backupStatus(FileBackupStatus.IN_PROGRESS)
                .filePath("tempFilePath");
    }

    public static class Builder {

        public FileDetails build() {
            var temp = internalBuild();
            return new FileDetails(
                    temp.id,
                    temp.gameId,
                    new SourceFileDetails(
                            temp.sourceId,
                            temp.originalGameTitle,
                            temp.fileTitle,
                            temp.version,
                            temp.url,
                            temp.originalFileName,
                            temp.size
                    ),
                    new BackupDetails(
                            temp.backupStatus,
                            temp.backupFailedReason,
                            temp.filePath
                    ),
                    temp.dateCreated,
                    temp.dateModified
            );
        }
    }
}
