package dev.codesoapbox.backity.core.discovery.application;

import lombok.NonNull;

public record GameContentDiscoveryStatus(
        @NonNull String gameProviderId,
        boolean isInProgress
) {
}
