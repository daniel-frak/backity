package dev.codesoapbox.backity.core.backup.domain.events;

import dev.codesoapbox.backity.core.gamefile.domain.FileBackupStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import jakarta.validation.constraints.NotNull;

public record FileBackupStatusChangedEvent(
        @NotNull GameFileId gameFileId,
        @NotNull FileBackupStatus newStatus,
        String failedReason
) implements DomainEvent {

    public static FileBackupStatusChangedEvent from(GameFile gameFile) {
        return new FileBackupStatusChangedEvent(
                gameFile.getId(),
                gameFile.getFileCopy().status(),
                gameFile.getFileCopy().failedReason()
        );
    }
}
