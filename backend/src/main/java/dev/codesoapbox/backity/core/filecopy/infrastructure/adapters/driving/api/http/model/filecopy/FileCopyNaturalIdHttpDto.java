package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model.filecopy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FileCopyNaturalId")
public record FileCopyNaturalIdHttpDto(
        @NotNull String gameFileId,
        @NotNull String backupTargetId
) {
}
