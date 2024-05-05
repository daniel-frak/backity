package dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;

@IncludeInDocumentation
@Schema(name = "LogCreatedMessage")
public record LogCreatedWsMessage(
        String message,
        Integer maxLogs
) {
}
