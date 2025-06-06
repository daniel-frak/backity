package dev.codesoapbox.backity.core.game.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy.FileCopyHttpDto;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.ProgressHttpDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FileCopyWithProgress")
public record FileCopyWithProgressHttpDto(
        @NotNull FileCopyHttpDto fileCopy,
        ProgressHttpDto progress
) {
}
