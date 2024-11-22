package dev.codesoapbox.backity.core.discovery.domain.events;

import lombok.NonNull;

public record FileDiscoveryProgressChangedEvent(
        @NonNull String gameProviderId,
        int percentage,
        long timeLeftSeconds
) {
}
