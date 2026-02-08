package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "SourceFileInFileCopyContext")
public record SourceFileInFileCopyContextHttpDto(
        @NotBlank String gameProviderId,
        @NotBlank String originalGameTitle,
        @NotBlank String fileTitle,
        @NotBlank String version,
        @NotBlank String url,
        @NotBlank String originalFileName,
        @NotBlank String size
) {
}
