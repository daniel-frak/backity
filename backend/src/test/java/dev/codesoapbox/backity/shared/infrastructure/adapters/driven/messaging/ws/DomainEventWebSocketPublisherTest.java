package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws;

import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DomainEventWebSocketPublisherTest {

    private DomainEventWebSocketPublisher eventPublisher;

    @Mock
    private DomainEventHandler<TestDomainEvent> testEventHandler;

    @Test
    void shouldPassEventToHandlerGivenHandlerExists() {
        eventPublisher = new DomainEventWebSocketPublisher();
        when(testEventHandler.getEventClass())
                .thenReturn(TestDomainEvent.class);
        eventPublisher.addHandlers(List.of(testEventHandler));
        var event = new TestDomainEvent();

        eventPublisher.publish(event);

        verify(testEventHandler, times(1)).handle(event);
    }

    @Test
    void shouldThrowExceptionGivenHandlerDoesNotExist() {
        eventPublisher = new DomainEventWebSocketPublisher();
        var event = new TestDomainEvent();

        assertThatThrownBy(() -> eventPublisher.publish(event))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No handler found for event type: " + event.getClass().getName());
    }

    static class TestDomainEvent implements DomainEvent {
    }
}
