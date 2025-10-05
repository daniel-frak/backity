package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxJpaSerializer;

import java.util.Map;

// @TODO Test
public class FileBackupFailedEventOutboxJpaSerializer 
        implements DomainEventOutboxJpaSerializer<FileBackupFailedEvent> {
    
    @Override
    public Map<String, Object> serialize(FileBackupFailedEvent event) {
        return Map.of(
                "fileCopyId", event.fileCopyId().value(),
                "gameFileId", event.fileCopyNaturalId().gameFileId().value(),
                "backupTargetId", event.fileCopyNaturalId().backupTargetId().value(),
                "failedReason", event.failedReason()
        );
    }

    @Override
    public FileBackupFailedEvent deserialize(Map<String, Object> eventData) {
        return new FileBackupFailedEvent(
                new FileCopyId((String) eventData.get("fileCopyId")),
                new FileCopyNaturalId(
                        new GameFileId((String) eventData.get("gameFileId")),
                        new BackupTargetId((String) eventData.get("backupTargetId"))
                ),
                (String) eventData.get("failedReason")
        );
    }

    @Override
    public Class<FileBackupFailedEvent> getSupportedEventClass() {
        return FileBackupFailedEvent.class;
    }
}
