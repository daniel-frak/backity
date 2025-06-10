package dev.codesoapbox.backity.core.discovery.application.usecases;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryOverview;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryProgressTracker;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class GetGameContentDiscoveryOverviewsUseCase {

    private final GameContentDiscoveryProgressTracker discoveryProgressTracker;

    public List<GameContentDiscoveryOverview> getDiscoveryOverviews() {
        return discoveryProgressTracker.getDiscoveryOverviews();
    }
}
