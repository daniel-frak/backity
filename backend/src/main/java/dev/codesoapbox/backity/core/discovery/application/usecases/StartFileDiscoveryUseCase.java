package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartFileDiscoveryUseCase {

    private final GameContentDiscoveryService gameContentDiscoveryService;

    public void startFileDiscovery() {
        gameContentDiscoveryService.startContentDiscovery();
    }
}
