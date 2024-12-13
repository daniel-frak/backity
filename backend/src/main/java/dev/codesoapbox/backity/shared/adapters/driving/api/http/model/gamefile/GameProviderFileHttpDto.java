package dev.codesoapbox.backity.shared.adapters.driving.api.http.model.gamefile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "GameProviderFile")
public record GameProviderFileHttpDto(
        @NotNull String gameProviderId,
        @NotNull String originalGameTitle,
        @NotNull String fileTitle,
        @NotNull String version,
        @NotNull String url,
        @NotNull String originalFileName,
        @NotNull String size
) {
}
