package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FileCopyWithContextWs")
public record FileCopyWithContextWsDto(
        @NotNull @Valid FileCopyWsDto fileCopy,
        @NotNull @Valid GameFileInFileCopyContextWsDto gameFile,
        @NotNull @Valid GameInFileCopyContextWsDto game
) {
}
