package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;

public class TestFileBackupEvents {

    private static final GameFileId GAME_FILE_ID = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");

    public static FileBackupStartedEvent started() {
        return new FileBackupStartedEvent(
                GAME_FILE_ID,
                "Original Game Title",
                "fileTitle",
                "1.0.0",
                "originalFileName",
                "5 KB",
                "file/path"
        );
    }

    public static FileBackupFailedEvent failed() {
        return new FileBackupFailedEvent(
                GAME_FILE_ID,
                "some failed reason"
        );
    }

    public static FileBackupFinishedEvent finished() {
        return new FileBackupFinishedEvent(
                GAME_FILE_ID
        );
    }

    public static FileBackupProgressChangedEvent progressChanged() {
        return new FileBackupProgressChangedEvent(
                50,
                999
        );
    }
}