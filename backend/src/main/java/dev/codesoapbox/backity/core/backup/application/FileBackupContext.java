package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTarget;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import lombok.NonNull;

public record FileBackupContext(
        @NonNull FileCopy fileCopy,
        @NonNull GameFile gameFile,
        @NonNull BackupTarget backupTarget,
        @NonNull StorageSolution storageSolution
) {
}
