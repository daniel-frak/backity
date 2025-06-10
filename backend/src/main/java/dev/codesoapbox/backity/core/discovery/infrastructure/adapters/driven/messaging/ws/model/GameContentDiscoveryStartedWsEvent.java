package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.NonNull;

@IncludeInDocumentation
@Schema(name = "GameContentDiscoveryStartedEvent")
public record GameContentDiscoveryStartedWsEvent(
        @NonNull @NotBlank String gameProviderId
) {
}
