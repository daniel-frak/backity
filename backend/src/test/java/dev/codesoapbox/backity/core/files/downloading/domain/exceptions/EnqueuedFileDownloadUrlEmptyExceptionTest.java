package dev.codesoapbox.backity.core.files.downloading.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnqueuedFileDownloadUrlEmptyExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new EnqueuedFileDownloadUrlEmptyException(1L);

        assertEquals("Game file url was null or empty for EnqueuedFileDownload with id: 1",
                exception.getMessage());
    }
}