package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;

public record EditBackupTargetCommand(
        BackupTargetId id,
        String name
) {
}
