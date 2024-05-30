package dev.codesoapbox.backity.core.discovery.adapters.application;

import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetFileDiscoveryStatusListUseCase {

    private final FileDiscoveryService fileDiscoveryService;

    public List<FileDiscoveryStatusChangedEvent> getStatusList() {
        return fileDiscoveryService.getStatuses();
    }
}
