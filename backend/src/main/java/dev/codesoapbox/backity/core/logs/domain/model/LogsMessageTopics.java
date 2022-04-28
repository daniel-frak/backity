package dev.codesoapbox.backity.core.logs.domain.model;

import dev.codesoapbox.backity.core.shared.config.openapi.IncludeInOpenApiDocs;
import lombok.AllArgsConstructor;

@IncludeInOpenApiDocs
@AllArgsConstructor
public enum LogsMessageTopics {

    LOGS("/topic/logs");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
