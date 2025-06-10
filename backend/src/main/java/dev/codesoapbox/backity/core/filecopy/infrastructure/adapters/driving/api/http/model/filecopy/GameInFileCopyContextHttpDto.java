package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "GameInFileCopyContext")
public record GameInFileCopyContextHttpDto(
        @NotBlank String title
) {
}
