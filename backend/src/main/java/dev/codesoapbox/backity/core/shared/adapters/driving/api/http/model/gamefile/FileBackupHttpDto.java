package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "FileBackup")
public record FileBackupHttpDto(
        @NotNull FileBackupStatusHttpDto status,
        String failedReason,
        String filePath
) {
}
