package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(name = "GameContentDiscoveryProgress")
public record GameContentDiscoveryProgressHttpDto(
        @NotNull int percentage,
        @NotNull long timeLeftSeconds,
        @NotNull long gamesDiscovered,
        @NotNull int gameFilesDiscovered
) {
}
