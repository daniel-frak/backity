package dev.codesoapbox.backity.core.discovery.domain.events;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import lombok.NonNull;

public record GameContentDiscoveryStatusChangedEvent(
        @NonNull GameProviderId gameProviderId,
        boolean isInProgress
) implements DomainEvent {
}
