package dev.codesoapbox.backity.core.files.downloading.adapters.driven.messaging;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import lombok.AllArgsConstructor;

@IncludeInDocumentation
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
