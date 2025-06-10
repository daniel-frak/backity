package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(name = "GameContentDiscoveryResult")
public record GameContentDiscoveryResultHttpDto(
        @NotNull LocalDateTime startedAt,
        @NotNull LocalDateTime stoppedAt,
        @NotNull GameContentDiscoveryOutcomeHttpDto discoveryOutcome,
        LocalDateTime lastSuccessfulDiscoveryCompletedAt,
        @NotNull int gamesDiscovered,
        @NotNull int gameFilesDiscovered
) {
}
