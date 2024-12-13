package dev.codesoapbox.backity.core.discovery.domain.events;

import dev.codesoapbox.backity.shared.domain.DomainEvent;
import lombok.NonNull;

public record FileDiscoveryStatusChangedEvent(
        @NonNull String gameProviderId,
        boolean isInProgress
) implements DomainEvent {
}
