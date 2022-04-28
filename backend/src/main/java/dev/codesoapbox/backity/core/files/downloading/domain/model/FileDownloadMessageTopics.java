package dev.codesoapbox.backity.core.files.downloading.domain.model;

import dev.codesoapbox.backity.core.shared.config.openapi.IncludeInOpenApiDocs;
import lombok.AllArgsConstructor;

@IncludeInOpenApiDocs
@AllArgsConstructor
public enum FileDownloadMessageTopics {

    DOWNLOAD_STARTED("/topic/downloads/started"),
    DOWNLOAD_FINISHED("/topic/downloads/finished");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
