package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;

public final class TestFileBackupWsEvent {

    private static final String GAME_FILE_ID = "acde26d7-33c7-42ee-be16-bca91a604b48";

    public static FileBackupStartedWsEvent started() {
        return new FileBackupStartedWsEvent(
                GAME_FILE_ID,
                "Original Game Title",
                "fileTitle",
                "1.0.0",
                "originalFileName",
                "5 KB",
                "file/path"
        );
    }

    public static FileBackupStatusChangedWsEvent startedAsStatusChange() {
        return new FileBackupStatusChangedWsEvent(
                GAME_FILE_ID,
                FileBackupStatus.IN_PROGRESS.name(),
                null
        );
    }

    public static FileBackupStatusChangedWsEvent failed() {
        return new FileBackupStatusChangedWsEvent(
                GAME_FILE_ID,
                FileBackupStatus.FAILED.name(),
                "some failed reason"
        );
    }

    public static FileBackupStatusChangedWsEvent finished() {
        return new FileBackupStatusChangedWsEvent(
                GAME_FILE_ID,
                FileBackupStatus.SUCCESS.name(),
                null
        );
    }

    public static FileBackupProgressUpdatedWsEvent progressChanged() {
        return new FileBackupProgressUpdatedWsEvent(
                50,
                999
        );
    }
}