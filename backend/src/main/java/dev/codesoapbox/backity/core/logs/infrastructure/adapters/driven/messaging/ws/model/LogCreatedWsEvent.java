package dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.model;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@IncludeInDocumentation
@Schema(name = "LogCreatedEvent")
public record LogCreatedWsEvent(
        @NotNull String message,
        @NotNull int maxLogs
) {
}
