package dev.codesoapbox.backity.core.files.adapters.driven.messaging.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SourceFileDetailsMessage")
public record SourceFileDetailsMessage(
        String sourceId,
        String originalGameTitle,
        String fileTitle,
        String version,
        String url,
        String originalFileName,
        String size
) {
}
