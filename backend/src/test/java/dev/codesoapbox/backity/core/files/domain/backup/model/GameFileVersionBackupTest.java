package dev.codesoapbox.backity.core.files.domain.backup.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileVersionBackupTest {

    @Test
    void shouldFail() {
        var enqueuedFileDownload = new GameFileVersionBackup(
                1L, "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB", null,
                null, FileBackupStatus.DISCOVERED, null);

        enqueuedFileDownload.fail("someFailedReason");

        assertEquals("someFailedReason", enqueuedFileDownload.getFailedReason());
        assertEquals(FileBackupStatus.FAILED, enqueuedFileDownload.getStatus());
    }

    @Test
    void shouldMarkAsDownloaded() {
        var enqueuedFileDownload = new GameFileVersionBackup(
                1L, "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB", null,
                null, FileBackupStatus.DISCOVERED, null);

        enqueuedFileDownload.markAsDownloaded("someFilePath");

        assertEquals("someFilePath", enqueuedFileDownload.getFilePath());
        assertEquals(FileBackupStatus.SUCCESS, enqueuedFileDownload.getStatus());
    }
}