package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameDownloadRequestFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new GameDownloadRequestFailedException("someUrl", "someMessage");

        assertEquals("An error occurred while downloading game file: someUrl. someMessage",
                exception.getMessage());
    }

    @Test
    void shouldGetCause() {
        var cause = new RuntimeException();
        var exception = new GameDownloadRequestFailedException("someId", cause);

        assertEquals(cause, exception.getCause());
    }
}