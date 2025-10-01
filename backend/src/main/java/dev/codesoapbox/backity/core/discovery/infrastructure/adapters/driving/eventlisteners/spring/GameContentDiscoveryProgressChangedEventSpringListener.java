package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.core.discovery.application.eventhandlers.GameContentDiscoveryProgressChangedEventHandler;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class GameContentDiscoveryProgressChangedEventSpringListener {

    private final GameContentDiscoveryProgressChangedEventHandler eventHandler;

    @EventListener
    public void handle(GameContentDiscoveryProgressChangedEvent event) {
        eventHandler.handle(event);
    }
}