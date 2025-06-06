package dev.codesoapbox.backity.core.backup.domain.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InvalidReplicationProgressPercentageExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new InvalidReplicationProgressPercentageException(999);

        var result = exception.getMessage();

        assertThat(result).isEqualTo("Percentage must be between 0 and 100, got: 999");
    }
}