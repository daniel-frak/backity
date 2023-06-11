package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BackupDetails")
public record BackupDetailsJson(

        FileBackupStatus status,
        String failedReason,
        String filePath
) {
}
