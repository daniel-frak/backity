package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class DomainEventForwardingSpringListenerIT {

    @Autowired
    private DomainEventForwardingHandler eventHandler;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void shouldHandleInternalEvent() {
        var event = new TestEvent("test");

        applicationEventPublisher.publishEvent(event);

        verify(eventHandler).handle(event);
    }

    @Test
    void shouldIgnoreGenericEvents() {
        var event = new Object();

        applicationEventPublisher.publishEvent(event);

        verify(eventHandler, never()).handle(event);
    }

    private record TestEvent(String value) {
    }
}