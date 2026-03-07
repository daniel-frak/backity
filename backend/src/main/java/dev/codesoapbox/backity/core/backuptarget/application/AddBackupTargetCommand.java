package dev.codesoapbox.backity.core.backuptarget.application;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;

public record AddBackupTargetCommand(
        String name,
        StorageSolutionId storageSolutionId,
        String pathTemplate
) {
}
