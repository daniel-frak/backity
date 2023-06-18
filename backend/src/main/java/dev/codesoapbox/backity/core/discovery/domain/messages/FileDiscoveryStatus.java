package dev.codesoapbox.backity.core.discovery.domain.messages;

import lombok.NonNull;

public record FileDiscoveryStatus(
        @NonNull String source,
        boolean isInProgress
) {
}
