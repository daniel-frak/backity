package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "GameContentDiscoveryOutcomeWs")
public enum GameContentDiscoveryOutcomeWsDto {
    SUCCESS, FAILED
}
