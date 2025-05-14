package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StopFileDiscoveryUseCase {

    private final GameContentDiscoveryService gameContentDiscoveryService;

    public void stopFileDiscovery() {
        gameContentDiscoveryService.stopContentDiscovery();
    }
}
