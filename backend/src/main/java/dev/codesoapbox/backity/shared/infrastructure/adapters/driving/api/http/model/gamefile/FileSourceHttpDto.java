package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FileSource")
public record FileSourceHttpDto(
        @NotNull String gameProviderId,
        @NotNull String originalGameTitle,
        @NotNull String fileTitle,
        @NotNull String version,
        @NotNull String url,
        @NotNull String originalFileName,
        @NotNull String size
) {
}
