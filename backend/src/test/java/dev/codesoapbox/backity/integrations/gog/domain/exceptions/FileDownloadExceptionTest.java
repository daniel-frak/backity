package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileDownloadExceptionTest {

    @Test
    void shouldGetMessage() {
        var expectedMessage = "someMessage";
        var exception = new FileDownloadException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void shouldGetMessageWithCause() {
        var expectedMessage = "someMessage";
        var expectedCause = new RuntimeException("someThrowable");
        var exception = new FileDownloadException(expectedMessage, expectedCause);

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedCause, exception.getCause());
    }
}