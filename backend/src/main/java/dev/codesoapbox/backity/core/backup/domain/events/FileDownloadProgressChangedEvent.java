package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;

import java.time.Duration;

public record FileDownloadProgressChangedEvent(
        FileCopyId fileCopyId,
        int percentage,
        Duration timeLeft
) implements DomainEvent {
}
