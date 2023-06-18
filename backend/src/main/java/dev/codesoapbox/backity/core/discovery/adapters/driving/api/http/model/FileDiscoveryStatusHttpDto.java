package dev.codesoapbox.backity.core.discovery.adapters.driving.api.http.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FileDiscoveryStatus")
public record FileDiscoveryStatusHttpDto(
        String source,
        boolean isInProgress
) {
}
