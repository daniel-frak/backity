package dev.codesoapbox.backity.core.discovery.domain.events;

import lombok.NonNull;

public record FileDiscoveryProgressChangedEvent(
        @NonNull String source,
        int percentage,
        long timeLeftSeconds
) {
}
