package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventlisteners;

import dev.codesoapbox.backity.core.discovery.application.eventhandlers.GameContentDiscoveryStoppedEventHandler;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import dev.codesoapbox.backity.testing.messaging.annotations.SpringEventListenerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.Mockito.verify;

@SpringEventListenerTest
class GameContentDiscoveryStoppedEventSpringListenerIT {

    @Autowired
    private GameContentDiscoveryStoppedEventHandler eventHandler;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void shouldPublishWebSocketEvent() {
        GameContentDiscoveryStoppedEvent event = TestGameContentDiscoveryEvent.discoveryStopped();

        applicationEventPublisher.publishEvent(event);

        verify(eventHandler).handle(event);
    }
}