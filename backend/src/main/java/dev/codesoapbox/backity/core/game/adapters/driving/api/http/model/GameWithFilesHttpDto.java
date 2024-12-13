package dev.codesoapbox.backity.core.game.adapters.driving.api.http.model;

import dev.codesoapbox.backity.shared.adapters.driving.api.http.model.gamefile.GameFileHttpDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "GameWithFiles")
public record GameWithFilesHttpDto(
        @NotNull String id,
        @NotNull String title,
        @NotNull List<GameFileHttpDto> files
) {
}
