package dev.codesoapbox.backity.core.gamefile.adapters.driving.api.http.controllers.model;

import dev.codesoapbox.backity.core.shared.adapters.driving.api.http.openapi.lowercaseenums.LowercaseApiEnum;
import io.swagger.v3.oas.annotations.media.Schema;

@LowercaseApiEnum
@Schema(name = "GameFileProcessingStatus")
public enum GameFileProcessingStatusHttpDto {

    DISCOVERED, ENQUEUED, PROCESSED
}
