package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.gamefiledetails;

import dev.codesoapbox.backity.core.gamefiledetails.domain.FileBackupStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BackupDetails")
public record BackupDetailsHttpDto(

        FileBackupStatus status,
        String failedReason,
        String filePath
) {
}
