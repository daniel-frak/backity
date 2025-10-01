package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.discovery.application.eventhandlers.GameContentDiscoveryStoppedEventHandler;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class GameContentDiscoveryStoppedEventSpringListener {

    private final GameContentDiscoveryStoppedEventHandler eventHandler;

    @EventListener
    public void handle(GameContentDiscoveryStoppedEvent event) {
        eventHandler.handle(event);
    }
}