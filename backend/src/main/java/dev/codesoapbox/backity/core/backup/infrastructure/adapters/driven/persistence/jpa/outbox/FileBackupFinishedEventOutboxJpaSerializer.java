package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyStatus;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxJpaSerializer;

import java.util.Map;

// @TODO Test
public class FileBackupFinishedEventOutboxJpaSerializer
        implements DomainEventOutboxJpaSerializer<FileBackupFinishedEvent> {

    @Override
    public Map<String, Object> serialize(FileBackupFinishedEvent event) {
        return Map.of(
                "fileCopyId", event.fileCopyId().value(),
                "gameFileId", event.fileCopyNaturalId().gameFileId().value(),
                "backupTargetId", event.fileCopyNaturalId().backupTargetId().value(),
                "newStatus", event.newStatus().name()
        );
    }

    @Override
    public FileBackupFinishedEvent deserialize(Map<String, Object> eventData) {
        return new FileBackupFinishedEvent(
                new FileCopyId((String) eventData.get("fileCopyId")),
                new FileCopyNaturalId(
                        new GameFileId((String) eventData.get("gameFileId")),
                        new BackupTargetId((String) eventData.get("backupTargetId"))
                ),
                FileCopyStatus.valueOf((String) eventData.get("newStatus"))
        );
    }

    @Override
    public Class<FileBackupFinishedEvent> getSupportedEventClass() {
        return FileBackupFinishedEvent.class;
    }
}
