package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(name = "GameFile")
public record GameFileHttpDto(
        @NotBlank String id,
        @NotBlank String gameId,
        @NotNull FileSourceHttpDto fileSource,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
