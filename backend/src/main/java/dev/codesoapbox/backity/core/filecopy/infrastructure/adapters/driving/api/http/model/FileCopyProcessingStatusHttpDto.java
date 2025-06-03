package dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.lowercaseenums.openapi.LowercaseApiEnum;
import io.swagger.v3.oas.annotations.media.Schema;

@LowercaseApiEnum
@Schema(name = "FileCopyProcessingStatus")
public enum FileCopyProcessingStatusHttpDto {

    ENQUEUED
}
