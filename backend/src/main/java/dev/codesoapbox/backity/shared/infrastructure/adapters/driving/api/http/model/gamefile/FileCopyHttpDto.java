package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FileCopy")
public record FileCopyHttpDto(
        @NotNull FileBackupStatusHttpDto status,
        String failedReason,
        String filePath
) {
}
