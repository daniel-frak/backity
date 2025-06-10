package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@IncludeInDocumentation
@Schema(name = "FileCopyStatusChangedEvent")
public record FileCopyStatusChangedWsEvent(
        @NotBlank String fileCopyId,
        @NotNull FileCopyNaturalIdWsDto fileCopyNaturalId,
        @NotBlank String newStatus,
        String failedReason
) {
}
