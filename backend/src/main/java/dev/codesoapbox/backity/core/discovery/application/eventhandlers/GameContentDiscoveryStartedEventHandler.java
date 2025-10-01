package dev.codesoapbox.backity.core.discovery.application.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameContentDiscoveryStartedEventHandler {

    private final GameContentDiscoveryStartedEventExternalForwarder eventForwarder;

    public void handle(GameContentDiscoveryStartedEvent event) {
        eventForwarder.forward(event);
    }
}
