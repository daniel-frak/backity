package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetName;

public record EditBackupTargetCommand(
        BackupTargetId id,
        BackupTargetName name
) {
}
