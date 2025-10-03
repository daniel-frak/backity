package dev.codesoapbox.backity.shared.application.eventhandlers;

import dev.codesoapbox.backity.shared.application.eventhandlers.exceptions.DomainEventForwardingHandlerException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventForwardingHandlerExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new DomainEventForwardingHandlerException(TestEvent.class);

        String result = exception.getMessage();

        assertThat(result).isEqualTo("One or more forwarders failed for event class: TestEvent");
    }

    static class TestEvent {
    }
}