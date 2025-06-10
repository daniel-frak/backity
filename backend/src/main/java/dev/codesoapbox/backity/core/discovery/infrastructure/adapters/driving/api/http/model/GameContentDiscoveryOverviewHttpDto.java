package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

@Schema(name = "GameContentDiscoveryOverview")
public record GameContentDiscoveryOverviewHttpDto(
        @NonNull @NotBlank String gameProviderId,
        @NotNull boolean isInProgress,
        @Valid GameContentDiscoveryProgressHttpDto progress,
        @Valid GameContentDiscoveryResultHttpDto lastDiscoveryResult
) {
}
