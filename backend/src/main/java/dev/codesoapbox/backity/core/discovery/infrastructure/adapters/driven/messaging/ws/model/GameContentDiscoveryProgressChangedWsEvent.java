package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

@IncludeInDocumentation
@Schema(name = "GameContentDiscoveryProgressUpdateEvent")
public record GameContentDiscoveryProgressChangedWsEvent(
        @NonNull @NotNull String gameProviderId,
        @NotNull int percentage,
        @NotNull long timeLeftSeconds
) {
}
