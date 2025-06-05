package dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Schema(name = "StorageSolutionStatusesResponse")
public record StorageSolutionStatusesResponseHttpDto(
        @NotNull Map<String, @Valid StorageSolutionStatusHttpDto> statuses
) {
}
