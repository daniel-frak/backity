package dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventMapperNotFoundExceptionTest {

    @Test
    void shouldGetMessage() {
        var exception = new OutboxEventMapperNotFoundException(TestEvent.class);

        String result = exception.getMessage();

        assertThat(result).isEqualTo("No OutboxEventMapper found for domain event type: "
                + TestEvent.class.getName());
    }

    record TestEvent(String value) {
    }
}