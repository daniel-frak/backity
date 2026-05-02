package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import lombok.NonNull;

public record FileCopyEnqueuedEvent(
        @NonNull FileCopyId fileCopyId
) implements DomainEvent {
}
