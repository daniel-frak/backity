package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetName;
import dev.codesoapbox.backity.core.backuptarget.domain.PathTemplate;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;

public record AddBackupTargetCommand(
        BackupTargetName name,
        StorageSolutionId storageSolutionId,
        PathTemplate pathTemplate
) {
}
