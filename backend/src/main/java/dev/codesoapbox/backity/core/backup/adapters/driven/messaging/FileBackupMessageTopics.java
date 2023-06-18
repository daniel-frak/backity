package dev.codesoapbox.backity.core.backup.adapters.driven.messaging;

import dev.codesoapbox.backity.core.shared.domain.IncludeInDocumentation;
import lombok.AllArgsConstructor;

@IncludeInDocumentation
@AllArgsConstructor
public enum FileBackupMessageTopics {

    BACKUP_STARTED("/topic/backups/started"),
    BACKUP_PROGRESS_UPDATE("/topic/backups/progress-update"),
    BACKUP_STATUS_CHANGED("/topic/backups/status-changed");

    private final String value;

    @Override
    public String toString() {
        return value;
    }
}
