package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import lombok.AllArgsConstructor;

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
