package dev.codesoapbox.backity.core.files.domain.backup.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileDetailsTest {

    @Test
    void shouldFail() {
        var enqueuedFileDownload = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB", null,
                null, FileBackupStatus.DISCOVERED, null);

        enqueuedFileDownload.fail("someFailedReason");

        assertEquals("someFailedReason", enqueuedFileDownload.getBackupFailedReason());
        assertEquals(FileBackupStatus.FAILED, enqueuedFileDownload.getBackupStatus());
    }

    @Test
    void shouldMarkAsDownloaded() {
        var enqueuedFileDownload = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB", null,
                null, FileBackupStatus.DISCOVERED, null);

        enqueuedFileDownload.markAsDownloaded("someFilePath");

        assertEquals("someFilePath", enqueuedFileDownload.getFilePath());
        assertEquals(FileBackupStatus.SUCCESS, enqueuedFileDownload.getBackupStatus());
    }
}