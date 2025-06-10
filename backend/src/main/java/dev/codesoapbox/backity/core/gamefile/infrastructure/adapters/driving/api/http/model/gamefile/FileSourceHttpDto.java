package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "FileSource")
public record FileSourceHttpDto(
        @NotBlank String gameProviderId,
        @NotBlank String originalGameTitle,
        @NotBlank String fileTitle,
        @NotBlank String version,
        @NotBlank String url,
        @NotBlank String originalFileName,
        @NotBlank String size
) {
}
