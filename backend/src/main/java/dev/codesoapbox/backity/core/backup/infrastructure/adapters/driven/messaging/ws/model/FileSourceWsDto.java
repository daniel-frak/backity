package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FileSourceWs")
public record FileSourceWsDto(
        @NotNull String gameProviderId,
        @NotNull String originalGameTitle,
        @NotNull String fileTitle,
        @NotNull String version,
        @NotNull String url,
        @NotNull String originalFileName,
        @NotNull String size
) {
}
