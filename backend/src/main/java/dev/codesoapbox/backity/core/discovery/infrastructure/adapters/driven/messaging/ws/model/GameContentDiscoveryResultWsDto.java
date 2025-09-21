package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(name = "GameContentDiscoveryResultWs")
public record GameContentDiscoveryResultWsDto(
        @NotNull LocalDateTime startedAt,
        @NotNull LocalDateTime stoppedAt,
        @NotNull GameContentDiscoveryOutcomeWsDto discoveryOutcome,
        LocalDateTime lastSuccessfulDiscoveryCompletedAt,
        @NotNull long gamesDiscovered,
        @NotNull int gameFilesDiscovered
) {
}
