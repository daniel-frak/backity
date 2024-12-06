package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.shared.domain.DomainEvent;
import jakarta.validation.constraints.NotNull;

public record FileBackupStartedEvent(
        @NotNull GameFileId gameFileId,
        @NotNull String originalGameTitle,
        @NotNull String fileTitle,
        @NotNull String version,
        @NotNull String originalFileName,
        @NotNull String size,
        String filePath
) implements DomainEvent {
}
