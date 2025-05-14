package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryStatus;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetFileDiscoveryStatusListUseCase {

    private final GameContentDiscoveryService gameContentDiscoveryService;

    public List<FileDiscoveryStatus> getStatusList() {
        return gameContentDiscoveryService.getStatuses();
    }
}
