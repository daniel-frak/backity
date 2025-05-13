package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.shared.domain.DomainEvent;

public record FileDownloadProgressChangedEvent(
        int percentage,
        long timeLeftSeconds
) implements DomainEvent {
}
