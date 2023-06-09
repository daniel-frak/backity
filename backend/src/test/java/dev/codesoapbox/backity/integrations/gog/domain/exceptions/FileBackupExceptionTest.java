package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackupExceptionTest {

    @Test
    void shouldGetMessage() {
        var expectedMessage = "someMessage";
        var exception = new FileBackupException(expectedMessage);

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void shouldGetMessageWithCause() {
        var expectedMessage = "someMessage";
        var expectedCause = new RuntimeException("someThrowable");
        var exception = new FileBackupException(expectedMessage, expectedCause);

        assertEquals(expectedMessage, exception.getMessage());
        assertEquals(expectedCause, exception.getCause());
    }
}