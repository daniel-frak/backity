package dev.codesoapbox.backity.core.discovery.domain.events;

import lombok.NonNull;

public record FileDiscoveryStatusChangedEvent(
        @NonNull String source,
        boolean isInProgress
) {
}
