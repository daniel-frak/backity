package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.embed.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameBackupRequestFailedExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new GameBackupRequestFailedException("http://some.url", "someMessage");

        String result = exception.getMessage();

        assertThat(result).isEqualTo("An error occurred while backing up file: http://some.url. someMessage");
    }

    @Test
    void shouldGetCause() {
        var cause = new RuntimeException();
        var exception = new GameBackupRequestFailedException("someId", cause);

        Throwable result = exception.getCause();

        assertThat(result).isSameAs(cause);
    }
}