package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.IncludeInDocumentation;
import lombok.AllArgsConstructor;

@SuppressWarnings("squid:S6548")
@IncludeInDocumentation
@AllArgsConstructor
public enum LogsMessageTopics {

    LOGS("/topic/logs");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
