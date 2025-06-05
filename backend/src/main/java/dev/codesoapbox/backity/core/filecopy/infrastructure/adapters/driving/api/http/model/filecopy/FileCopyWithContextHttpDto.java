package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.ProgressHttpDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FileCopyWithContext")
public record FileCopyWithContextHttpDto(
        @NotNull @Valid FileCopyHttpDto fileCopy,
        @NotNull @Valid GameFileInFileCopyContextHttpDto gameFile,
        @NotNull @Valid GameInFileCopyContextHttpDto game,
        @NotNull @Valid BackupTargetInFileCopyContextHttpDto backupTarget,
        @Valid ProgressHttpDto progress
) {
}
