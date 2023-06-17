package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SourceFileDetails")
public record SourceFileDetailsHttpDto(
        String sourceId,
        String originalGameTitle,
        String fileTitle,
        String version,
        String url,
        String originalFileName,
        String size
) {
}
