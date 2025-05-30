package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@IncludeInDocumentation
@Schema(name = "GameFileDiscoveredEvent")
public record GameFileDiscoveredWsEvent(
        String originalGameTitle,
        String originalFileName,
        String fileTitle,
        String size
) {
}
