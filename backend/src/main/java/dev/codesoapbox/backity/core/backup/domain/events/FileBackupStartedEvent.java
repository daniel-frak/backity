package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.gamefile.domain.FileSize;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

public record FileBackupStartedEvent(
        @NotNull GameFileId gameFileId,
        @NotNull String originalGameTitle,
        @NotNull String fileTitle,
        @NotNull String version,
        @NotNull String originalFileName,
        @NonNull FileSize size,
        String filePath
) implements DomainEvent {
}
