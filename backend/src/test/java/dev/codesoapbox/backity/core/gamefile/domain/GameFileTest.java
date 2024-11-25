package dev.codesoapbox.backity.core.gamefile.domain;

import dev.codesoapbox.backity.core.gamefile.domain.exceptions.GameFileNotBackedUpException;
import org.junit.jupiter.api.Test;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.successfulGameFile;
import static org.assertj.core.api.Assertions.*;

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

    @Test
    void validateIsBackedUpShouldDoNothingGivenStatusIsSuccessful() {
        GameFile gameFile = successfulGameFile().build();

        assertThatCode(gameFile::validateIsBackedUp)
                .doesNotThrowAnyException();
    }

    @Test
    void validateIsBackedUpShouldThrowGivenStatusIsNotSuccessful() {
        GameFile gameFile = discoveredGameFile().build();

        assertThatThrownBy(gameFile::validateIsBackedUp)
                .isInstanceOf(GameFileNotBackedUpException.class)
                .hasMessageContaining(gameFile.getId().toString());
    }
}