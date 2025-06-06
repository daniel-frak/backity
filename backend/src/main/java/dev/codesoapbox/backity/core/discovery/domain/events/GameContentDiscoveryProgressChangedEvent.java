package dev.codesoapbox.backity.core.discovery.domain.events;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import lombok.NonNull;

import java.time.Duration;

public record GameContentDiscoveryProgressChangedEvent(
        @NonNull GameProviderId gameProviderId,
        int percentage,
        @NonNull Duration timeLeft
) implements DomainEvent {
}
