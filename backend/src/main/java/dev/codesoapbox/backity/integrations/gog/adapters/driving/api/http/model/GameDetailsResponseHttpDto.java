package dev.codesoapbox.backity.integrations.gog.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "GameDetailsResponse")
public record GameDetailsResponseHttpDto(
        String title,
        String backgroundImage,
        String cdKey,
        String textInformation,
        List<GameFileResponseHttpDto> files,
        String changelog
) {
}
