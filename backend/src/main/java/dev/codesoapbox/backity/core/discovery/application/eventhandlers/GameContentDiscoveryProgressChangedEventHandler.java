package dev.codesoapbox.backity.core.discovery.application.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameContentDiscoveryProgressChangedEventHandler {

    private final GameContentDiscoveryProgressChangedEventExternalForwarder eventForwarder;

    public void handle(GameContentDiscoveryProgressChangedEvent event) {
        eventForwarder.forward(event);
    }
}
