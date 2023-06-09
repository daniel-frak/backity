package dev.codesoapbox.backity.core.files.domain.backup.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileVersionBackupTest {

    @Test
    void shouldFail() {
        var enqueuedFileDownload = new GameFileVersionBackup();

        enqueuedFileDownload.fail("someFailedReason");

        assertEquals("someFailedReason", enqueuedFileDownload.getFailedReason());
        assertEquals(FileBackupStatus.FAILED, enqueuedFileDownload.getStatus());
    }

    @Test
    void shouldMarkAsDownloaded() {
        var enqueuedFileDownload = new GameFileVersionBackup();

        enqueuedFileDownload.markAsDownloaded("someFilePath");

        assertEquals("someFilePath", enqueuedFileDownload.getFilePath());
        assertEquals(FileBackupStatus.SUCCESS, enqueuedFileDownload.getStatus());
    }
}