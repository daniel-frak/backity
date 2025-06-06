package dev.codesoapbox.backity.core.game.application;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import jakarta.validation.constraints.NotNull;

public record FileCopyWithProgress(
        @NotNull FileCopy fileCopy,
        FileCopyReplicationProgress progress
) {
}
