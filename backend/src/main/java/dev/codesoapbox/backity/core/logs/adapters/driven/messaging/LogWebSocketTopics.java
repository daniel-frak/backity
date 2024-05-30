package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

@SuppressWarnings("squid:S6548")
@Schema(name = "LogsMessageTopics")
@IncludeInDocumentation
@AllArgsConstructor
public enum LogWebSocketTopics {

    LOGS("/topic/logs");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
