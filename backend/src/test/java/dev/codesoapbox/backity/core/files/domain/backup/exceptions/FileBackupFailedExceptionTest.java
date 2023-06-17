package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import org.junit.jupiter.api.Test;

import static dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails.discovered;
import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackupFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        GameFileDetails gameFileDetails = discovered().build();

        var exception = new FileBackupFailedException(gameFileDetails, null);

        assertEquals("Could not back up game file acde26d7-33c7-42ee-be16-bca91a604b48",
                exception.getMessage());
    }

    @Test
    void shouldGetCause() {
        GameFileDetails gameFileDetails = discovered().build();
        var cause = new RuntimeException("test");

        var exception = new FileBackupFailedException(gameFileDetails, cause);

        assertEquals(cause, exception.getCause());
    }
}