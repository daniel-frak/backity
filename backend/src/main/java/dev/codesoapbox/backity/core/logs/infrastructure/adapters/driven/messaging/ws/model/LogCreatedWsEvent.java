package dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@IncludeInDocumentation
@Schema(name = "LogCreatedEvent")
public record LogCreatedWsEvent(
        String message,
        Integer maxLogs
) {
}
