package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.filecopy.domain.FileBackupStatus;

public final class TestFileBackupWsEvent {

    private static final String GAME_FILE_ID = "acde26d7-33c7-42ee-be16-bca91a604b48";
    private static final String FILE_COPY_ID = "6df888e8-90b9-4df5-a237-0cba422c0310";

    public static FileBackupStartedWsEvent started() {
        return new FileBackupStartedWsEvent(
                FILE_COPY_ID,
                GAME_FILE_ID,
                "Game 1",
                "Game 1 (Installer)",
                "1.0.0",
                "game_1_installer.exe",
                "5 KB",
                "file/path"
        );
    }

    public static FileBackupStatusChangedWsEvent startedAsStatusChange() {
        return new FileBackupStatusChangedWsEvent(
                FILE_COPY_ID,
                GAME_FILE_ID,
                FileBackupStatus.IN_PROGRESS.name(),
                null
        );
    }

    public static FileBackupStatusChangedWsEvent failed() {
        return new FileBackupStatusChangedWsEvent(
                FILE_COPY_ID,
                GAME_FILE_ID,
                FileBackupStatus.FAILED.name(),
                "some failed reason"
        );
    }

    public static FileBackupStatusChangedWsEvent finished() {
        return new FileBackupStatusChangedWsEvent(
                FILE_COPY_ID,
                GAME_FILE_ID,
                FileBackupStatus.SUCCESS.name(),
                null
        );
    }

    public static FileDownloadProgressUpdatedWsEvent progressChanged() {
        return new FileDownloadProgressUpdatedWsEvent(
                50,
                999
        );
    }
}