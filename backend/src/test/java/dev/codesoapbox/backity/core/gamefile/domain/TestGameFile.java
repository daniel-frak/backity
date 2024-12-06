package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.game.domain.GameId;

import java.time.LocalDateTime;
import java.util.ArrayList;

@lombok.Builder(builderClassName = "Builder", buildMethodName = "internalBuild",
        builderMethodName = "discoveredGameFile")
public class TestGameFile {

    @lombok.Builder.Default
    private GameFileId id = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");

    @lombok.Builder.Default
    private GameId gameId = new GameId("1eec1c19-25bf-4094-b926-84b5bb8fa281");

    @lombok.Builder.Default
    private GameProviderId gameProviderId = new GameProviderId("someGameProviderId");

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

    public static Builder fullGameFile() {
        return discoveredGameFile()
                .backupFailedReason("someFailedReason")
                .filePath("someFilePath");
    }

    public static Builder discoveredGameFile() {
        return new Builder();
    }

    public static Builder successfulGameFile() {
        return discoveredGameFile()
                .backupStatus(FileBackupStatus.SUCCESS)
                .filePath("someFilePath");
    }

    public static Builder enqueuedGameFile() {
        return discoveredGameFile()
                .backupStatus(FileBackupStatus.ENQUEUED);
    }

    public static Builder failedGameFile() {
        return discoveredGameFile()
                .backupStatus(FileBackupStatus.FAILED)
                .backupFailedReason("someFailedReason");
    }

    public static Builder inProgressGameFile() {
        return discoveredGameFile()
                .backupStatus(FileBackupStatus.IN_PROGRESS)
                .filePath("tempFilePath");
    }

    public static class Builder {

        public GameFile build() {
            var temp = internalBuild();
            return new GameFile(
                    temp.id,
                    temp.gameId,
                    new GameProviderFile(
                            temp.gameProviderId,
                            temp.originalGameTitle,
                            temp.fileTitle,
                            temp.version,
                            temp.url,
                            temp.originalFileName,
                            temp.size
                    ),
                    new FileBackup(
                            temp.backupStatus,
                            temp.backupFailedReason,
                            temp.filePath
                    ),
                    temp.dateCreated,
                    temp.dateModified,
                    new ArrayList<>()
            );
        }
    }
}
