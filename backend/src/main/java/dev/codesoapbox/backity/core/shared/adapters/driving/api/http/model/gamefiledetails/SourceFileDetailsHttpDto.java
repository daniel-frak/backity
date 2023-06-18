package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails;

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
