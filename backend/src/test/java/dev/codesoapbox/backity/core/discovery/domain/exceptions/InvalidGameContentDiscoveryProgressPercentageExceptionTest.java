package dev.codesoapbox.backity.core.discovery.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidGameContentDiscoveryProgressPercentageExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new InvalidGameContentDiscoveryProgressPercentageException(999);

        String result = exception.getMessage();

        assertThat(result).isEqualTo("Percentage must be between 0 and 100, got: 999");
    }
}