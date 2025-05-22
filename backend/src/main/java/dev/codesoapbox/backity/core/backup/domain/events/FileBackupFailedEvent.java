package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import jakarta.validation.constraints.NotNull;

public record FileBackupFailedEvent(
        @NotNull FileCopyId fileCopyId,
        @NotNull GameFileId gameFileId,
        @NotNull String failedReason
) implements DomainEvent {
}
