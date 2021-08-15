package dev.codesoapbox.backity.core.files.discovery.application;

import dev.codesoapbox.backity.core.shared.infrastructure.config.openapi.IncludeInOpenApiDocs;
import lombok.AllArgsConstructor;

@IncludeInOpenApiDocs
@AllArgsConstructor
public enum FileDiscoveryMessageTopics {

    FILE_DISCOVERY("/topic/file-discovery");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
