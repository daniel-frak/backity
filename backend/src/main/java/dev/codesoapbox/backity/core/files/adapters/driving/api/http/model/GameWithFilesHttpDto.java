package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "GameWithFiles")
public record GameWithFilesHttpDto(
        String id,
        String title,
        List<GameFileDetailsHttpDto> gameFiles
) {
}
