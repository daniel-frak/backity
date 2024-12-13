package dev.codesoapbox.backity.core.logs.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@IncludeInDocumentation
@Schema(name = "LogCreatedEvent")
public record LogCreatedWsEvent(
        String message,
        Integer maxLogs
) {
}
