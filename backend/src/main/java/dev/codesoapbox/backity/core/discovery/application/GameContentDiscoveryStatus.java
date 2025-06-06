package dev.codesoapbox.backity.core.discovery.application;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgress;
import lombok.NonNull;

public record GameContentDiscoveryStatus(
        @NonNull GameProviderId gameProviderId,
        boolean isInProgress,
        GameContentDiscoveryProgress progress
) {
}
