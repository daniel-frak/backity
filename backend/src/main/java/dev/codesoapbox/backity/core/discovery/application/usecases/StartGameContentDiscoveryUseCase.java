package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartGameContentDiscoveryUseCase {

    private final GameContentDiscoveryService gameContentDiscoveryService;

    public void startContentDiscovery() {
        gameContentDiscoveryService.startContentDiscovery();
    }
}
