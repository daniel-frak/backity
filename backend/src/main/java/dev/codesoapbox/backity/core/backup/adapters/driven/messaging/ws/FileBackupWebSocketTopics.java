package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws;

import dev.codesoapbox.backity.shared.adapters.driven.messaging.IncludeInDocumentation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Schema(name = "FileBackupMessageTopics")
@IncludeInDocumentation
@AllArgsConstructor
@Accessors(fluent = true)
@Getter
public enum FileBackupWebSocketTopics {

    BACKUP_STARTED("/topic/backups/started"),
    BACKUP_PROGRESS_CHANGED("/topic/backups/progress-update"),
    BACKUP_STATUS_CHANGED("/topic/backups/status-changed");

    private final String wsDestination;
}