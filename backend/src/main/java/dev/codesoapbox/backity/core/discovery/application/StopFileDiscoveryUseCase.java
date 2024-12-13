package dev.codesoapbox.backity.core.discovery.application;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StopFileDiscoveryUseCase {

    private final FileDiscoveryService fileDiscoveryService;

    public void stopFileDiscovery() {
        fileDiscoveryService.stopFileDiscovery();
    }
}
