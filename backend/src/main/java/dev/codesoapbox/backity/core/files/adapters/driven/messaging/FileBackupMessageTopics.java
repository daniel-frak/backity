package dev.codesoapbox.backity.core.files.adapters.driven.messaging;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import lombok.AllArgsConstructor;

@IncludeInDocumentation
@AllArgsConstructor
public enum FileBackupMessageTopics {

    DOWNLOAD_STARTED("/topic/backups/started"),
    DOWNLOAD_PROGRESS("/topic/backups/progress"),
    DOWNLOAD_FINISHED("/topic/backups/finished");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
