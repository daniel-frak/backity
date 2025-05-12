package dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "GogGameWithFiles")
public record GogGameWithFilesHttpDto(
        String title,
        String backgroundImage,
        String cdKey,
        String textInformation,
        List<GameFileHttpDto> files,
        String changelog
) {
}
