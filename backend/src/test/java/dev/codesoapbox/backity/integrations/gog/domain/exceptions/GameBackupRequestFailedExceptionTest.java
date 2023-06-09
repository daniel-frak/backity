package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameBackupRequestFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new GameBackupRequestFailedException("someUrl", "someMessage");

        assertEquals("An error occurred while downloading game file: someUrl. someMessage",
                exception.getMessage());
    }

    @Test
    void shouldGetCause() {
        var cause = new RuntimeException();
        var exception = new GameBackupRequestFailedException("someId", cause);

        assertEquals(cause, exception.getCause());
    }
}