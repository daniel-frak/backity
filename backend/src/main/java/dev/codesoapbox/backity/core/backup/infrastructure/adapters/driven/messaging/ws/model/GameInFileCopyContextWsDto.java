package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "GameInFileCopyContextWs")
public record GameInFileCopyContextWsDto(
        @NotNull String title
) {
}
