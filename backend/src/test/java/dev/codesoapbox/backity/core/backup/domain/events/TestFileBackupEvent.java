package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;

public final class TestFileBackupEvent {

    private static final GameFileId GAME_FILE_ID = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");
    private static final FileCopyId FILE_COPY_ID = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");

    public static FileBackupStartedEvent started() {
        return new FileBackupStartedEvent(
                FILE_COPY_ID,
                GAME_FILE_ID,
                "file/path"
        );
    }

    public static FileBackupFailedEvent failed() {
        return new FileBackupFailedEvent(
                FILE_COPY_ID,
                GAME_FILE_ID,
                "some failed reason"
        );
    }

    public static FileBackupFinishedEvent finished() {
        return new FileBackupFinishedEvent(
                FILE_COPY_ID,
                GAME_FILE_ID
        );
    }

    public static FileDownloadProgressChangedEvent progressChanged() {
        return new FileDownloadProgressChangedEvent(
                50,
                999
        );
    }
}