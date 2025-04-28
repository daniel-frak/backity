package dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driving.api.http.controllers.model;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.lowercaseenums.openapi.LowercaseApiEnum;
import io.swagger.v3.oas.annotations.media.Schema;

@LowercaseApiEnum
@Schema(name = "GameFileProcessingStatus")
public enum GameFileProcessingStatusHttpDto {

    DISCOVERED, ENQUEUED, PROCESSED
}
