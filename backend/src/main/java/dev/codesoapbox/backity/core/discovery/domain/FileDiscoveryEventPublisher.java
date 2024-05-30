package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;

public interface FileDiscoveryEventPublisher {

    void publishStatusChangedEvent(FileDiscoveryStatusChangedEvent status);

    void publishProgressChangedEvent(FileDiscoveryProgressChangedEvent progress);

    void publishFileDiscoveredEvent(FileDetails payload);
}
