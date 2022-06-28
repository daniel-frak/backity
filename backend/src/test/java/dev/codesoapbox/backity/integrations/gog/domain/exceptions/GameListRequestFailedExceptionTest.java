package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameListRequestFailedExceptionTest {

    @Test
    void shouldGetCause() {
        var cause = new RuntimeException();
        var exception = new GameListRequestFailedException(cause);

        assertEquals(cause, exception.getCause());
    }
}