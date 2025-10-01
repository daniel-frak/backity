package dev.codesoapbox.backity.core.discovery.application.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;

public interface GameContentDiscoveryProgressChangedEventExternalForwarder {

    void forward(GameContentDiscoveryProgressChangedEvent event);
}
