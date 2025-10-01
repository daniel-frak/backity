package dev.codesoapbox.backity.core.discovery.application.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameContentDiscoveryStoppedEventHandler {

    private final GameContentDiscoveryStoppedEventExternalForwarder eventForwarder;

    public void handle(GameContentDiscoveryStoppedEvent event) {
        eventForwarder.forward(event);
    }
}
