package dev.codesoapbox.backity.core.files.domain.backup.model;

import org.junit.jupiter.api.Test;

import static dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails.discovered;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GameFileDetailsTest {

    @Test
    void shouldFail() {
        GameFileDetails gameFileDetails = discovered().build();

        gameFileDetails.fail("someFailedReason");

        assertEquals("someFailedReason", gameFileDetails.getBackupDetails().getFailedReason());
        assertEquals(FileBackupStatus.FAILED, gameFileDetails.getBackupDetails().getStatus());
    }

    @Test
    void shouldMarkAsDownloaded() {
        GameFileDetails gameFileDetails = discovered().build();

        gameFileDetails.markAsDownloaded("someFilePath");

        assertEquals("someFilePath", gameFileDetails.getBackupDetails().getFilePath());
        assertEquals(FileBackupStatus.SUCCESS, gameFileDetails.getBackupDetails().getStatus());
    }
}