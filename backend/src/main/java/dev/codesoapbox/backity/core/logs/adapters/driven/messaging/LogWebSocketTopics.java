package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@SuppressWarnings("squid:S6548")
@Schema(name = "LogsMessageTopics")
@IncludeInDocumentation
@AllArgsConstructor
@Accessors(fluent = true)
@Getter
public enum LogWebSocketTopics {

    LOGS("/topic/logs");

    private final String wsDestination;
}
