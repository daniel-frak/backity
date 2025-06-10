package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "BackupTargetInFileCopyContext")
public record BackupTargetInFileCopyContextHttpDto(
        @NotBlank String name,
        @NotBlank String storageSolutionId
) {
}
