package dev.codesoapbox.backity.core.discovery.domain.events;

import lombok.NonNull;

public record FileDiscoveryStatusChangedEvent(
        @NonNull String gameProviderId,
        boolean isInProgress
) {
}
