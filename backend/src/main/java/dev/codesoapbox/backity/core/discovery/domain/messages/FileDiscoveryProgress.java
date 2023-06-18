package dev.codesoapbox.backity.core.discovery.domain.messages;

import lombok.NonNull;

public record FileDiscoveryProgress(
        @NonNull String source,
        int percentage,
        long timeLeftSeconds
) {
}
