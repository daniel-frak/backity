package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameListRequestFailedExceptionTest {

    @Test
    void shouldGetCause() {
        var cause = new RuntimeException();
        var exception = new GameListRequestFailedException(cause);

        Throwable result = exception.getCause();

        assertThat(result).isSameAs(cause);
    }
}