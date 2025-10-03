package dev.codesoapbox.backity.shared.infrastructure.config.events;

import dev.codesoapbox.backity.shared.infrastructure.config.events.exceptions.DomainEventForwarderException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventForwarderExceptionTest {

    @Test
    void shouldGetCause() {
        var expectedCause = new RuntimeException("Test exception");
        var exception = new DomainEventForwarderException(expectedCause);

        Throwable result = exception.getCause();

        assertThat(result).isEqualTo(expectedCause);
    }
}