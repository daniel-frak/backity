package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartFileDiscoveryUseCase {

    private final FileDiscoveryService fileDiscoveryService;

    public void startFileDiscovery() {
        fileDiscoveryService.startFileDiscovery();
    }
}
