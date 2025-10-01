package dev.codesoapbox.backity.core.discovery.application.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;

public interface GameContentDiscoveryStoppedEventExternalForwarder {

    void forward(GameContentDiscoveryStoppedEvent event);
}
