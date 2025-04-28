package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;

@IncludeInDocumentation
@Schema(name = "FileDiscoveryStatusChangedEvent")
public record FileDiscoveryStatusChangedWsEvent(
        @NonNull @NotNull String gameProviderId,
        boolean isInProgress
) {
}
