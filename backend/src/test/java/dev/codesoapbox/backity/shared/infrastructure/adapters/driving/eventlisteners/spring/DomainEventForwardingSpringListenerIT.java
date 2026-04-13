package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import dev.codesoapbox.backity.testing.messaging.inmemory.InMemoryEventScenario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class DomainEventForwardingSpringListenerIT {

    @Autowired
    private DomainEventForwardingHandler eventHandler;

    @Test
    void shouldHandleInternalEvent(InMemoryEventScenario scenario) {
        var event = new TestEvent("test");

        scenario.publish(event)
                .thenVerifyAsync(() -> verify(eventHandler).handle(event));
    }

    @Test
    void shouldIgnoreGenericEvents(InMemoryEventScenario scenario) {
        var event = new Object();

        scenario.publish(event)
                .thenVerifyAsync(() -> verify(eventHandler, never()).handle(event));
    }

    private record TestEvent(String value) {
    }
}