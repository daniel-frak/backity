package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GameFileDetailsResponse")
public record GameFileDetailsResponseHttpDto(
        String version,
        String manualUrl,
        String name,
        String size,
        String fileTitle
) {
}
