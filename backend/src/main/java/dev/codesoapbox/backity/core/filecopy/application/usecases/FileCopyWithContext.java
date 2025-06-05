package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import lombok.NonNull;

public record FileCopyWithContext(
        @NonNull FileCopy fileCopy,
        @NonNull GameFile gameFile,
        @NonNull Game game,
        @NonNull BackupTarget backupTarget,
        FileCopyReplicationProgress progress
) {
}
