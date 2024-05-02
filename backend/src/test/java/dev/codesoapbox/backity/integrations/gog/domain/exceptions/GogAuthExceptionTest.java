package dev.codesoapbox.backity.integrations.gog.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GogAuthExceptionTest {

    @Test
    void shouldGetMessage() {
        var message = "someMessage";
        var exception = new GogAuthException(message);

        String result = exception.getMessage();

        assertThat(result).isEqualTo(message);
    }
}