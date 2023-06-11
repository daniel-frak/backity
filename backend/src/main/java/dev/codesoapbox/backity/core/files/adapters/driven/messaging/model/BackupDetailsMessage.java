package dev.codesoapbox.backity.core.files.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.files.domain.backup.model.FileBackupStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BackupDetailsMessage")
public record BackupDetailsMessage(

        FileBackupStatus status,
        String failedReason,
        String filePath
) {
}
