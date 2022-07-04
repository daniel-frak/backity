package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameDetailsRequestFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new GameDetailsRequestFailedException("someId", "someMessage");

        assertEquals("Could not retrieve game details for game id: someId. someMessage",
                exception.getMessage());
    }

    @Test
    void shouldGetMessageAndCause() {
        var cause = new RuntimeException();
        var exception = new GameDetailsRequestFailedException("someId", cause);

        assertEquals(cause, exception.getCause());
        assertEquals("Could not retrieve game details for game id: someId", exception.getMessage());
    }
}