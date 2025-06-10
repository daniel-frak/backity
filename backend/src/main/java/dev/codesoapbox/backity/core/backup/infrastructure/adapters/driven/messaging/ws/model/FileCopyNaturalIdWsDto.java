package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import jakarta.validation.constraints.NotBlank;

public record FileCopyNaturalIdWsDto(
        @NotBlank String gameFileId,
        @NotBlank String backupTargetId
) {
}
