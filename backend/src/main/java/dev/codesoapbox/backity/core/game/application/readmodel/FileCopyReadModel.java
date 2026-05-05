package dev.codesoapbox.backity.core.game.application.readmodel;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePath;

import java.time.LocalDateTime;

public record FileCopyReadModel(
        String id,
        FileCopyNaturalIdReadModel naturalId,
        FileCopyStatus status,
        String failedReason,
        FilePath filePath,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
