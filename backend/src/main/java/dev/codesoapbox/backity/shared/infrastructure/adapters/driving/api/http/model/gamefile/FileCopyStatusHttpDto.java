package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.gamefile;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FileCopyStatus")
public enum FileCopyStatusHttpDto {

    DISCOVERED, ENQUEUED, IN_PROGRESS, SUCCESS, FAILED
}
