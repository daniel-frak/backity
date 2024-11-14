package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "SourceFileDetails")
public record SourceFileDetailsHttpDto(
        @NotNull String sourceId,
        @NotNull String originalGameTitle,
        @NotNull String fileTitle,
        @NotNull String version,
        @NotNull String url,
        @NotNull String originalFileName,
        @NotNull String size
) {
}
