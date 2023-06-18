package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.NonNull;

@IncludeInDocumentation
@Schema(name = "FileDiscoveryProgressUpdateMessage")
public record FileDiscoveryProgressUpdateWsMessage(
        @NonNull String source,
        int percentage,
        long timeLeftSeconds
) {
}
