package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameDetailsRequestFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new GameDetailsRequestFailedException("someId", null);

        assertEquals("Could not retrieve game details for game id: someId", exception.getMessage());
    }

    @Test
    void shouldGetCause() {
        var cause = new RuntimeException();
        var exception = new GameDetailsRequestFailedException("someId", cause);

        assertEquals(cause, exception.getCause());
    }
}