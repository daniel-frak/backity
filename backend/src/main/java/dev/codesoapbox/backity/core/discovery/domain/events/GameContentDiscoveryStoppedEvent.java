package dev.codesoapbox.backity.core.discovery.domain.events;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResult;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import lombok.NonNull;

public record GameContentDiscoveryStoppedEvent(
        @NonNull GameProviderId gameProviderId,
        @NonNull GameContentDiscoveryResult discoveryResult
) implements DomainEvent {
}
