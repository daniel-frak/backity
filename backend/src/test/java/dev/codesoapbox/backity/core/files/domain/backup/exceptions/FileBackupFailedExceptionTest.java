package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

import dev.codesoapbox.backity.core.files.domain.backup.model.TestGameFileDetails;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackupFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var gameFileDetails = TestGameFileDetails.GAME_FILE_DETAILS_1.get();

        var exception = new FileBackupFailedException(gameFileDetails, null);

        assertEquals("Could not back up game file acde26d7-33c7-42ee-be16-bca91a604b48",
                exception.getMessage());
    }

    @Test
    void shouldGetCause() {
        var enqueuedFileDownload = TestGameFileDetails.GAME_FILE_DETAILS_1.get();
        var cause = new RuntimeException("test");

        var exception = new FileBackupFailedException(enqueuedFileDownload, cause);

        assertEquals(cause, exception.getCause());
    }
}