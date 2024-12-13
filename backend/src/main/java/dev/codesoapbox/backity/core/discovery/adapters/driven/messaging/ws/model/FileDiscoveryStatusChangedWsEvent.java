package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@IncludeInDocumentation
@Schema(name = "FileDiscoveryStatusChangedEvent")
public record FileDiscoveryStatusChangedWsEvent(
        @NonNull String gameProviderId,
        boolean isInProgress
) {
}
