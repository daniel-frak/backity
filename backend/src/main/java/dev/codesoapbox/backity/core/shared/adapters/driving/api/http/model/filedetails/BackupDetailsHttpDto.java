package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BackupDetails")
public record BackupDetailsHttpDto(

        FileBackupStatusHttpDto status,
        String failedReason,
        String filePath
) {
}
