package dev.codesoapbox.backity.core.discovery.application;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartFileDiscoveryUseCase {

    private final FileDiscoveryService fileDiscoveryService;

    public void startFileDiscovery() {
        fileDiscoveryService.startFileDiscovery();
    }
}
