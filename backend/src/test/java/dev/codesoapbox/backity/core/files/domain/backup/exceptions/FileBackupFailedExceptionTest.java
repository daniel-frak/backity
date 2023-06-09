package dev.codesoapbox.backity.core.files.domain.backup.exceptions;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersionBackup;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackupFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var enqueuedFileDownload = new GameFileVersionBackup(
                1L, "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB", null,
                null, FileBackupStatus.DISCOVERED, null);

        var exception = new FileBackupFailedException(enqueuedFileDownload, null);

        assertEquals("Could not back up game file 1", exception.getMessage());
    }

    @Test
    void shouldGetCause() {
        var enqueuedFileDownload = new GameFileVersionBackup(
                1L, "someSource", "someUrl", "someTitle", "someOriginalFileName",
                null, null, "someGameId", "someVersion", "100 KB", null,
                null, FileBackupStatus.DISCOVERED, null);
        var cause = new RuntimeException("test");

        var exception = new FileBackupFailedException(enqueuedFileDownload, cause);

        assertEquals(cause, exception.getCause());
    }
}