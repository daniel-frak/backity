package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@IncludeInDocumentation
@Schema(name = "FileDiscoveredEvent")
public record FileDiscoveredWsEvent(
        String originalGameTitle,
        String originalFileName,
        String fileTitle,
        String size
) {
}
