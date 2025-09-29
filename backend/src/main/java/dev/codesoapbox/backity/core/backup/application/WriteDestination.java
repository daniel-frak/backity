package dev.codesoapbox.backity.core.backup.application;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionId;

public record WriteDestination(
        StorageSolutionId storageSolutionId,
        String filePath
) {
}
