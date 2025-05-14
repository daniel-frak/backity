package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryStatus;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetGameContentDiscoveryStatusListUseCase {

    private final GameContentDiscoveryService gameContentDiscoveryService;

    public List<GameContentDiscoveryStatus> getStatusList() {
        return gameContentDiscoveryService.getStatuses();
    }
}
