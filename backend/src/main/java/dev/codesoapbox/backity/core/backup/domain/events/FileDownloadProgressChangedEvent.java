package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import lombok.NonNull;

import java.time.Duration;

public record FileDownloadProgressChangedEvent(
        @NonNull FileCopyId fileCopyId,
        @NonNull FileCopyNaturalId fileCopyNaturalId,
        int percentage,
        @NonNull Duration timeLeft
) implements DomainEvent {
}
