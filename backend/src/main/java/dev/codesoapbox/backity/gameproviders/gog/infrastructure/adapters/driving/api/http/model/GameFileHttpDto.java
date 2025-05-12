package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GameFile")
public record GameFileHttpDto(
        String version,
        String manualUrl,
        String name,
        String size,
        String fileTitle
) {
}
