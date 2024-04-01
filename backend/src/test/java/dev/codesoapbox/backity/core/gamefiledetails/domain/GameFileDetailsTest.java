package dev.codesoapbox.backity.core.gamefiledetails.domain;

import org.junit.jupiter.api.Test;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.discoveredFileDetails;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileDetailsTest {

    @Test
    void shouldEnqueue() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();

        gameFileDetails.enqueue();

        assertEquals(FileBackupStatus.ENQUEUED, gameFileDetails.getBackupDetails().getStatus());
    }

    @Test
    void shouldFail() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();

        gameFileDetails.fail("someFailedReason");

        assertEquals("someFailedReason", gameFileDetails.getBackupDetails().getFailedReason());
        assertEquals(FileBackupStatus.FAILED, gameFileDetails.getBackupDetails().getStatus());
    }

    @Test
    void shouldMarkAsDownloaded() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();

        gameFileDetails.markAsDownloaded("someFilePath");

        assertEquals("someFilePath", gameFileDetails.getBackupDetails().getFilePath());
        assertEquals(FileBackupStatus.SUCCESS, gameFileDetails.getBackupDetails().getStatus());
    }
}