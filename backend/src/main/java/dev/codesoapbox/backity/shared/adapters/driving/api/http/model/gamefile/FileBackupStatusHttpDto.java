package dev.codesoapbox.backity.shared.adapters.driving.api.http.model.gamefile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FileBackupStatus")
public enum FileBackupStatusHttpDto {

    DISCOVERED, ENQUEUED, IN_PROGRESS, SUCCESS, FAILED
}
