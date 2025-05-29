package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.model.gamefile.FileSourceHttpDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "GameFileInFileCopyContext")
public record GameFileInFileCopyContextHttpDto(
        @NotNull FileSourceHttpDto fileSource
) {
}
