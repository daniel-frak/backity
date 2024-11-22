package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;

public interface FileDiscoveryEventPublisher {

    void publishStatusChangedEvent(FileDiscoveryStatusChangedEvent status);

    void publishProgressChangedEvent(FileDiscoveryProgressChangedEvent progress);

    void publishFileDiscoveredEvent(GameFile payload);
}
