package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;

public final class TestFileBackupWsEvent {

    private static final String GAME_FILE_ID = "acde26d7-33c7-42ee-be16-bca91a604b48";
    private static final String FILE_COPY_ID = "6df888e8-90b9-4df5-a237-0cba422c0310";
    private static final String BACKUP_TARGET_ID = "d46dde81-e519-4300-9a54-6f9e7d637926";
    private static final FileCopyNaturalIdWsDto FILE_COPY_NATURAL_ID =
            new FileCopyNaturalIdWsDto(GAME_FILE_ID, BACKUP_TARGET_ID);

    public static FileCopyStatusChangedWsEvent started() {
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

    public static FileCopyStatusChangedWsEvent finishedIntegrityUnknown() {
        return new FileCopyStatusChangedWsEvent(
                FILE_COPY_ID,
                FILE_COPY_NATURAL_ID,
                FileCopyStatus.STORED_INTEGRITY_UNKNOWN.name(),
                null
        );
    }

    public static FileCopyReplicationProgressUpdatedWsEvent progressChanged() {
        return new FileCopyReplicationProgressUpdatedWsEvent(
                FILE_COPY_ID,
                FILE_COPY_NATURAL_ID,
                50,
                999
        );
    }
}