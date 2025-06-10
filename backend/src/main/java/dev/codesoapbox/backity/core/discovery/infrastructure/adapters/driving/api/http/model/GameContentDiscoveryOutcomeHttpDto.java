package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GameContentDiscoveryOutcome")
public enum GameContentDiscoveryOutcomeHttpDto {
    SUCCESS, FAILED
}
