package dev.codesoapbox.backity.core.filecopy.domain;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import lombok.NonNull;

public record FileCopyNaturalId(
        @NonNull GameFileId gameFileId,
        @NonNull BackupTargetId backupTargetId
) {
}
