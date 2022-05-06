package dev.codesoapbox.backity.core.files.downloading.domain.exceptions;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileDownloadFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder()
                .id(1L)
                .build();

        var exception = new FileDownloadFailedException(enqueuedFileDownload, null);

        assertEquals("Could not download game file 1", exception.getMessage());
    }

    @Test
    void shouldGetCause() {
        var enqueuedFileDownload = EnqueuedFileDownload.builder()
                .id(1L)
                .build();
        var cause = new RuntimeException("test");

        var exception = new FileDownloadFailedException(enqueuedFileDownload, cause);

        assertEquals(cause, exception.getCause());
    }
}