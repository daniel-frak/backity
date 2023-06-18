package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@IncludeInDocumentation
@Schema(name = "FileDiscoveredMessage")
public record FileDiscoveredMessage(
        String originalGameTitle,
        String originalFileName,
        String fileTitle,
        String size
) {
}
