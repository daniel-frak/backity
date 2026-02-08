package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Schema(name = "GameFile")
public record GameFileHttpDto(
        @NotBlank String id,
        @NotBlank String gameId,
        @NotBlank String gameProviderId,
        @NotBlank String originalGameTitle,
        @NotBlank String fileTitle,
        @NotBlank String version,
        @NotBlank String url,
        @NotBlank String originalFileName,
        @NotBlank String size,
        LocalDateTime dateCreated,
        LocalDateTime dateModified
) {
}
