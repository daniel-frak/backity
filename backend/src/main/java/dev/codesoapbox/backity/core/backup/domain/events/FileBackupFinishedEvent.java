package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.core.shared.domain.DomainEvent;
import jakarta.validation.constraints.NotNull;

public record FileBackupFinishedEvent(
        @NotNull GameFileId gameFileId
) implements DomainEvent {
}
