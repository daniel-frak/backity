package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.shared.domain.DomainEvent;

public record FileBackupProgressChangedEvent(
        int percentage,
        long timeLeftSeconds
) implements DomainEvent {
}