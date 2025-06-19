package dev.codesoapbox.backity.core.game.application.readmodel;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;

import java.time.LocalDateTime;

public record FileCopyReadModel(
        String id,
        FileCopyNaturalIdReadModel naturalId,
        FileCopyStatus status,
        String failedReason,
        String filePath,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
