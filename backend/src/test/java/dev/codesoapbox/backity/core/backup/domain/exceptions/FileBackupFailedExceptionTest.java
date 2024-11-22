package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import org.junit.jupiter.api.Test;

import static dev.codesoapbox.backity.core.gamefile.domain.TestGameFile.discoveredGameFile;
import static org.assertj.core.api.Assertions.assertThat;

class FileBackupFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        GameFile gameFile = discoveredGameFile().build();

        var exception = new FileBackupFailedException(gameFile, null);

        assertThat(exception.getMessage())
                .isEqualTo("Could not back up game file acde26d7-33c7-42ee-be16-bca91a604b48");
    }

    @Test
    void shouldGetCause() {
        GameFile gameFile = discoveredGameFile().build();
        var cause = new RuntimeException("test");

        var exception = new FileBackupFailedException(gameFile, cause);

        assertThat(exception.getCause()).isSameAs(cause);
    }
}