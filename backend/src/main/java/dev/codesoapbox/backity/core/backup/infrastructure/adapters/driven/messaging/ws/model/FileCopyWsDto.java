package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(name = "FileCopyWs")
public record FileCopyWsDto(
        @NotNull String id,
        @NotNull FileCopyNaturalIdWsDto naturalId,
        @NotNull FileCopyStatusWsDto status,
        String failedReason,
        String filePath,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
