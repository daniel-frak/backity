package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileDownloadExceptionTest {

    @Test
    void shouldGetMessage() {
        var expectedMessage = "someMessage";
        var exception = new FileDownloadException(expectedMessage, new RuntimeException("someThrowable"));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void shouldGetCause() {
        var expectedCause = new RuntimeException("someThrowable");
        var exception = new FileDownloadException("someMessage", expectedCause);

        assertEquals(expectedCause, exception.getCause());
    }
}