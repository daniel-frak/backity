package dev.codesoapbox.backity.core.gamefiledetails.domain;

import org.junit.jupiter.api.Test;

import static dev.codesoapbox.backity.core.gamefiledetails.domain.TestGameFileDetails.discoveredFileDetails;
import static org.assertj.core.api.Assertions.assertThat;

class GameFileDetailsTest {

    @Test
    void shouldEnqueue() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();

        gameFileDetails.enqueue();

        assertThat(gameFileDetails.getBackupDetails().getStatus()).isEqualTo(FileBackupStatus.ENQUEUED);
    }

    @Test
    void shouldFail() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();

        gameFileDetails.fail("someFailedReason");

        assertThat(gameFileDetails.getBackupDetails().getFailedReason()).isEqualTo("someFailedReason");
        assertThat(gameFileDetails.getBackupDetails().getStatus()).isEqualTo(FileBackupStatus.FAILED);
    }

    @Test
    void shouldMarkAsDownloaded() {
        GameFileDetails gameFileDetails = discoveredFileDetails().build();

        gameFileDetails.markAsDownloaded("someFilePath");

        assertThat(gameFileDetails.getBackupDetails().getFilePath()).isEqualTo("someFilePath");
        assertThat(gameFileDetails.getBackupDetails().getStatus()).isEqualTo(FileBackupStatus.SUCCESS);
    }
}