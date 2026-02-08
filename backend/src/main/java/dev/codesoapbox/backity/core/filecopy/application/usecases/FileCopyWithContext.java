package dev.codesoapbox.backity.core.filecopy.application.usecases;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgress;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.sourcefile.domain.SourceFile;
import lombok.NonNull;

public record FileCopyWithContext(
        @NonNull FileCopy fileCopy,
        @NonNull SourceFile sourceFile,
        @NonNull Game game,
        @NonNull BackupTarget backupTarget,
        FileCopyReplicationProgress progress
) {
}
