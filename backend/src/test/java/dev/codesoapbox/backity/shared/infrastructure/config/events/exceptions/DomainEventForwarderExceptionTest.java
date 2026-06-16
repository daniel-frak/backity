package dev.codesoapbox.backity.shared.infrastructure.config.events.exceptions;

import dev.codesoapbox.backity.testing.TestException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventForwarderExceptionTest {

    @Test
    void shouldGetCause() {
        var expectedCause = new TestException();
        var exception = new DomainEventForwarderException(expectedCause);

        Throwable result = exception.getCause();

        assertThat(result).isEqualTo(expectedCause);
    }
}