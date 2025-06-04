package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;

public record FileCopyWithContext(
        FileCopy fileCopy,
        GameFile gameFile,
        Game game,
        BackupTarget backupTarget
) {
}
