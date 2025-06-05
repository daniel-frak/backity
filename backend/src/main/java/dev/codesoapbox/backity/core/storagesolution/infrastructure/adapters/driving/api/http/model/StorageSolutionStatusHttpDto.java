package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "StorageSolutionStatus")
public enum StorageSolutionStatusHttpDto {
    NOT_CONNECTED, CONNECTED
}
