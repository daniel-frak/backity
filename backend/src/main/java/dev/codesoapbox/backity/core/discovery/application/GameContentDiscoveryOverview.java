package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import lombok.NonNull;

public record GameContentDiscoveryOverview(
        @NonNull GameProviderId gameProviderId,
        boolean isInProgress,
        GameContentDiscoveryProgress progress,
        GameContentDiscoveryResult lastDiscoveryResult
) {
}
