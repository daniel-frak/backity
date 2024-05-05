package dev.codesoapbox.backity.core.shared.adapters.driving.api.http.model.filedetails;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FileBackupStatus")
public enum FileBackupStatusHttpDto {

    DISCOVERED, ENQUEUED, IN_PROGRESS, SUCCESS, FAILED
}
