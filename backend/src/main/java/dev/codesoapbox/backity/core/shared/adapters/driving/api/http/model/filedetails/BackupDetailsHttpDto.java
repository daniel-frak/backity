package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "BackupDetails")
public record BackupDetailsHttpDto(
        @NotNull FileBackupStatusHttpDto status,
        String failedReason,
        String filePath
) {
}
