package dev.codesoapbox.backity.core.discovery.adapters.application;

import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetFileDiscoveryStatusListUseCase {

    private final FileDiscoveryService fileDiscoveryService;

    public List<FileDiscoveryStatus> getStatusList() {
        return fileDiscoveryService.getStatuses();
    }
}
