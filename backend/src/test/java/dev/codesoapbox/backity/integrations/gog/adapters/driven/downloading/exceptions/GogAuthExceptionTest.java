package dev.codesoapbox.backity.integrations.gog.adapters.driven.downloading.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GogAuthExceptionTest {

    @Test
    void shouldGetMessage() {
        var message = "someMessage";

        var exception = new GogAuthException(message);

        assertEquals(message, exception.getMessage());
    }
}