package dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@IncludeInDocumentation
@Schema(name = "LogCreatedMessage")
public record LogCreatedWsEvent(
        String message,
        Integer maxLogs
) {
}
