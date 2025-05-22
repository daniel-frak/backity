package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "GameWithFileCopies")
public record GameWithFileCopiesHttpDto(
        @NotNull String id,
        @NotNull String title,
        @NotNull List<GameFileWithCopiesHttpDto> gameFilesWithCopies
) {
}
