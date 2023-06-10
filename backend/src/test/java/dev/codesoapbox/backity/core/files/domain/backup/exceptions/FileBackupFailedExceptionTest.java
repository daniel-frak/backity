package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetailsId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackupFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var enqueuedFileDownload = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB", null,
                null, FileBackupStatus.DISCOVERED, null);

        var exception = new FileBackupFailedException(enqueuedFileDownload, null);

        assertEquals("Could not back up game file acde26d7-33c7-42ee-be16-bca91a604b48",
                exception.getMessage());
    }

    @Test
    void shouldGetCause() {
        var enqueuedFileDownload = new GameFileDetails(
                new GameFileDetailsId(UUID.fromString("acde26d7-33c7-42ee-be16-bca91a604b48")),
                "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB", null,
                null, FileBackupStatus.DISCOVERED, null);
        var cause = new RuntimeException("test");

        var exception = new FileBackupFailedException(enqueuedFileDownload, cause);

        assertEquals(cause, exception.getCause());
    }
}