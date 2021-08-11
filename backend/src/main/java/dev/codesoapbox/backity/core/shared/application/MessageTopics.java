package dev.codesoapbox.backity.core.shared.application;

import dev.codesoapbox.backity.core.shared.infrastructure.config.openapi.IncludeInOpenApiDocs;
import lombok.AllArgsConstructor;

@IncludeInOpenApiDocs
@AllArgsConstructor
public enum MessageTopics {

    FILE_DISCOVERY("/topic/file-discovery"),
    LOGS("/topic/logs");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
