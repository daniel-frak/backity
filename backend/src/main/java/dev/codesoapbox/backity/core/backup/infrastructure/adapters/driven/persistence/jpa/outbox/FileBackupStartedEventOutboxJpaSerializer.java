package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxJpaSerializer;

import java.util.Map;

// @TODO Test
public class FileBackupStartedEventOutboxJpaSerializer
        implements DomainEventOutboxJpaSerializer<FileBackupStartedEvent> {

    @Override
    public Map<String, Object> serialize(FileBackupStartedEvent event) {
        return Map.of(
                "fileCopyId", event.fileCopyId().value(),
                "gameFileId", event.fileCopyNaturalId().gameFileId().value(),
                "backupTargetId", event.fileCopyNaturalId().backupTargetId().value(),
                "filePath", event.filePath()
        );
    }

    @Override
    public FileBackupStartedEvent deserialize(Map<String, Object> eventData) {
        return new FileBackupStartedEvent(
                new FileCopyId((String) eventData.get("fileCopyId")),
                new FileCopyNaturalId(
                        new GameFileId((String) eventData.get("gameFileId")),
                        new BackupTargetId((String) eventData.get("backupTargetId"))
                ),
                (String) eventData.get("filePath")
        );
    }

    @Override
    public Class<FileBackupStartedEvent> getSupportedEventClass() {
        return FileBackupStartedEvent.class;
    }
}
