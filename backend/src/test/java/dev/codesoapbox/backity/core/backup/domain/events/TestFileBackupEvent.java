package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;

import java.time.Duration;

public final class TestFileBackupEvent {

    private static final GameFileId GAME_FILE_ID = new GameFileId("acde26d7-33c7-42ee-be16-bca91a604b48");
    private static final FileCopyId FILE_COPY_ID = new FileCopyId("6df888e8-90b9-4df5-a237-0cba422c0310");
    private static final BackupTargetId BACKUP_TARGET_ID =
            new BackupTargetId("d46dde81-e519-4300-9a54-6f9e7d637926");
    private static final FileCopyNaturalId FILE_COPY_NATURAL_ID = new FileCopyNaturalId(GAME_FILE_ID, BACKUP_TARGET_ID);

    public static FileBackupStartedEvent started() {
        return new FileBackupStartedEvent(
                FILE_COPY_ID,
                FILE_COPY_NATURAL_ID,
                "file/path"
        );
    }

    public static FileBackupFailedEvent failed() {
        return new FileBackupFailedEvent(
                FILE_COPY_ID,
                FILE_COPY_NATURAL_ID,
                "some failed reason"
        );
    }

    public static FileBackupFinishedEvent finishedIntegrityUnknown() {
        return new FileBackupFinishedEvent(
                FILE_COPY_ID,
                FILE_COPY_NATURAL_ID,
                FileCopyStatus.STORED_INTEGRITY_UNKNOWN
        );
    }

    public static FileDownloadProgressChangedEvent progressChanged() {
        return new FileDownloadProgressChangedEvent(
                FILE_COPY_ID,
                FILE_COPY_NATURAL_ID,
                50,
                Duration.ofSeconds(999)
        );
    }
}