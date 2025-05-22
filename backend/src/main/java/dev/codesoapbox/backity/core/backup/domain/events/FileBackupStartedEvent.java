package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import jakarta.validation.constraints.NotNull;

public record FileBackupStartedEvent(
        @NotNull FileCopyId fileCopyId,
        @NotNull GameFileId gameFileId,
        String filePath
) implements DomainEvent {
}
