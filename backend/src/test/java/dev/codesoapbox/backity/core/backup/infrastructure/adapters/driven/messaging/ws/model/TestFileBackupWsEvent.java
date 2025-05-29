package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;

import java.time.LocalDateTime;

public final class TestFileBackupWsEvent {

    private static final String GAME_FILE_ID = "acde26d7-33c7-42ee-be16-bca91a604b48";
    private static final String FILE_COPY_ID = "6df888e8-90b9-4df5-a237-0cba422c0310";
    private static final String BACKUP_TARGET_ID = "d46dde81-e519-4300-9a54-6f9e7d637926";
    private static final FileCopyNaturalIdWsDto FILE_COPY_NATURAL_ID =
            new FileCopyNaturalIdWsDto(GAME_FILE_ID, BACKUP_TARGET_ID);

    public static FileBackupStartedWsEvent started() {
        return new FileBackupStartedWsEvent(
                new FileCopyWithContextWsDto(
                        new FileCopyWsDto(
                                "6df888e8-90b9-4df5-a237-0cba422c0310",
                                new FileCopyNaturalIdWsDto(
                                        "acde26d7-33c7-42ee-be16-bca91a604b48",
                                        "eda52c13-ddf7-406f-97d9-d3ce2cab5a76"
                                ),
                                FileCopyStatusWsDto.IN_PROGRESS,
                                null,
                                "someFilePath",
                                LocalDateTime.parse("2022-04-29T14:15:53"),
                                LocalDateTime.parse("2023-04-29T14:15:53")
                        ),
                        new GameFileInFileCopyContextWsDto(
                                new FileSourceWsDto(
                                        "GOG",
                                        "Game 1",
                                        "Game 1 (Installer)",
                                        "1.0.0",
                                        "/downlink/some_game/some_file",
                                        "game_1_installer.exe",
                                        "5 KB"
                                )
                        ),
                        new GameInFileCopyContextWsDto(
                                "Test Game"
                        ))
        );
    }

    public static FileCopyStatusChangedWsEvent startedAsStatusChange() {
        return new FileCopyStatusChangedWsEvent(
                FILE_COPY_ID,
                FILE_COPY_NATURAL_ID,
                FileCopyStatus.IN_PROGRESS.name(),
                null
        );
    }

    public static FileCopyStatusChangedWsEvent failed() {
        return new FileCopyStatusChangedWsEvent(
                FILE_COPY_ID,
                FILE_COPY_NATURAL_ID,
                FileCopyStatus.FAILED.name(),
                "some failed reason"
        );
    }

    public static FileCopyStatusChangedWsEvent finished() {
        return new FileCopyStatusChangedWsEvent(
                FILE_COPY_ID,
                FILE_COPY_NATURAL_ID,
                FileCopyStatus.STORED_INTEGRITY_UNKNOWN.name(),
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