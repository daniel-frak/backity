package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.discovery.application.eventhandlers.GameContentDiscoveryStartedEventHandler;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class GameContentDiscoveryStartedEventSpringListener {

    private final GameContentDiscoveryStartedEventHandler eventHandler;

    @EventListener
    public void handle(GameContentDiscoveryStartedEvent event) {
        eventHandler.handle(event);
    }
}