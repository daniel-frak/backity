package dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "AddBackupTargetResponse")
public record AddBackupTargetHttpResponse(
        BackupTargetHttpDto backupTarget
) {
}
