package dev.codesoapbox.backity.core.discovery.application;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetFileDiscoveryStatusListUseCase {

    private final FileDiscoveryService fileDiscoveryService;

    public List<FileDiscoveryStatus> getStatusList() {
        return fileDiscoveryService.getStatuses();
    }
}
