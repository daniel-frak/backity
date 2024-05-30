package dev.codesoapbox.backity.core.backup.adapters.driven.messaging;

import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;

@Schema(name = "FileBackupMessageTopics")
@IncludeInDocumentation
@AllArgsConstructor
public enum FileBackupWebSocketTopics {

    BACKUP_STARTED("/topic/backups/started"),
    BACKUP_PROGRESS_UPDATE("/topic/backups/progress-update"),
    BACKUP_STATUS_CHANGED("/topic/backups/status-changed");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
