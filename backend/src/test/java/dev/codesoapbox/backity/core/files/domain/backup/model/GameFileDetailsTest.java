package dev.codesoapbox.backity.core.files.domain.backup.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileDetailsTest {

    @Test
    void shouldFail() {
        var gameFileDetails = TestGameFileDetails.GAME_FILE_DETAILS_1.get();

        gameFileDetails.fail("someFailedReason");

        assertEquals("someFailedReason", gameFileDetails.getBackupDetails().getFailedReason());
        assertEquals(FileBackupStatus.FAILED, gameFileDetails.getBackupDetails().getStatus());
    }

    @Test
    void shouldMarkAsDownloaded() {
        var enqueuedFileDownload = TestGameFileDetails.GAME_FILE_DETAILS_1.get();

        enqueuedFileDownload.markAsDownloaded("someFilePath");

        assertEquals("someFilePath", enqueuedFileDownload.getBackupDetails().getFilePath());
        assertEquals(FileBackupStatus.SUCCESS, enqueuedFileDownload.getBackupDetails().getStatus());
    }
}