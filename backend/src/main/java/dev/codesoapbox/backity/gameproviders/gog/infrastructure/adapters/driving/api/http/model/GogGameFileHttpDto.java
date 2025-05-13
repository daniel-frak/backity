package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GogGameFile")
public record GogGameFileHttpDto(
        String version,
        String manualUrl,
        String name,
        String size,
        String fileTitle
) {
}
