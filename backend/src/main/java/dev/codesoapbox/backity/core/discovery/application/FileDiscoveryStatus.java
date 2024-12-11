package dev.codesoapbox.backity.core.discovery.application;

import lombok.NonNull;

public record FileDiscoveryStatus(
        @NonNull String gameProviderId,
        boolean isInProgress
) {
}
