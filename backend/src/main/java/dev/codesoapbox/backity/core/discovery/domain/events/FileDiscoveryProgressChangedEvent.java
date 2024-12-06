package dev.codesoapbox.backity.core.discovery.domain.events;

import dev.codesoapbox.backity.core.shared.domain.DomainEvent;
import lombok.NonNull;

public record FileDiscoveryProgressChangedEvent(
        @NonNull String gameProviderId,
        int percentage,
        long timeLeftSeconds
) implements DomainEvent {
}
