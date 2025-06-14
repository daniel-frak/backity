package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import lombok.NonNull;

public record FileBackupFinishedEvent(
        @NonNull FileCopyId fileCopyId,
        @NonNull FileCopyNaturalId fileCopyNaturalId,
        @NonNull FileCopyStatus newStatus
) implements DomainEvent {
}
