package dev.codesoapbox.backity.core.gamefile.domain;

import org.junit.jupiter.api.Test;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static org.assertj.core.api.Assertions.assertThat;

class GameFileTest {

    @Test
    void shouldEnqueue() {
        GameFile gameFile = discoveredGameFile().build();

        gameFile.enqueue();

        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.ENQUEUED);
    }

    @Test
    void shouldFail() {
        GameFile gameFile = discoveredGameFile().build();

        gameFile.fail("someFailedReason");

        assertThat(gameFile.getFileBackup().getFailedReason()).isEqualTo("someFailedReason");
        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.FAILED);
    }

    @Test
    void shouldMarkAsDownloaded() {
        GameFile gameFile = discoveredGameFile().build();

        gameFile.markAsDownloaded("someFilePath");

        assertThat(gameFile.getFileBackup().getFilePath()).isEqualTo("someFilePath");
        assertThat(gameFile.getFileBackup().getStatus()).isEqualTo(FileBackupStatus.SUCCESS);
    }
}