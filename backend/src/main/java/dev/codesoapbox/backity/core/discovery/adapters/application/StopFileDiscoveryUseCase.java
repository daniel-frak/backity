package dev.codesoapbox.backity.core.discovery.adapters.application;

import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StopFileDiscoveryUseCase {

    private final FileDiscoveryService fileDiscoveryService;

    public void stopFileDiscovery() {
        fileDiscoveryService.stopFileDiscovery();
    }
}
