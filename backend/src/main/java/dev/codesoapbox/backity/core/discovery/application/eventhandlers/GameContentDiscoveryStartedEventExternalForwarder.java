package dev.codesoapbox.backity.core.discovery.application.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;

public interface GameContentDiscoveryStartedEventExternalForwarder {

    void forward(GameContentDiscoveryStartedEvent event);
}
