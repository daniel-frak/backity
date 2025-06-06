package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.ProgressHttpDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

@Schema(name = "GameContentDiscoveryStatus")
public record GameContentDiscoveryStatusHttpDto(
        @NonNull @NotNull String gameProviderId,
        @NotNull boolean isInProgress,
        @Valid ProgressHttpDto progress
) {
}
