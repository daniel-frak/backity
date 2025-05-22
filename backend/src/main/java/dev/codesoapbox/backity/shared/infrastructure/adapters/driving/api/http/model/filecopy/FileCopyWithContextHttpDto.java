package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.filecopy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FileCopyWithContext")
public record FileCopyWithContextHttpDto(
        @NotNull FileCopyHttpDto fileCopy,
        @NotNull GameFileInFileCopyContext gameFile,
        @NotNull GameInFileCopyContextHttpDto game
) {
}
