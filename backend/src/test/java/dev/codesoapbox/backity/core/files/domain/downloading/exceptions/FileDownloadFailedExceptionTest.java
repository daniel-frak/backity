package dev.codesoapbox.backity.core.files.domain.downloading.exceptions;

import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileDownloadFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var enqueuedFileDownload = GameFileVersion.builder()
                .id(1L)
                .build();

        var exception = new FileDownloadFailedException(enqueuedFileDownload, null);

        assertEquals("Could not download game file 1", exception.getMessage());
    }

    @Test
    void shouldGetCause() {
        var enqueuedFileDownload = GameFileVersion.builder()
                .id(1L)
                .build();
        var cause = new RuntimeException("test");

        var exception = new FileDownloadFailedException(enqueuedFileDownload, cause);

        assertEquals(cause, exception.getCause());
    }
}