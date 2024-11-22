package dev.codesoapbox.backity.core.discovery.domain;

import lombok.NonNull;

public record FileDiscoveryStatus(
        @NonNull String gameProviderId,
        boolean isInProgress
) {
}
