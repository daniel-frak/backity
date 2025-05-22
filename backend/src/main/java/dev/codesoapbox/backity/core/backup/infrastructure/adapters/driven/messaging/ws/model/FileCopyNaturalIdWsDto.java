package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import jakarta.validation.constraints.NotNull;

public record FileCopyNaturalIdWsDto(
        @NotNull String gameFileId,
        @NotNull String backupTargetId
) {
}
