package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GogFile")
public record GogFileHttpDto(
        String version,
        String manualUrl,
        String fileTitle,
        String size,
        String fileName
) {
}
