package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyNaturalId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxJpaSerializer;

import java.time.Duration;
import java.util.Map;

// @TODO Test
public class FileCopyReplicationProgressChangedEventOutboxJpaSerializer
        implements DomainEventOutboxJpaSerializer<FileCopyReplicationProgressChangedEvent> {

    @Override
    public Map<String, Object> serialize(FileCopyReplicationProgressChangedEvent event) {
        return Map.of(
                "fileCopyId", event.fileCopyId().value(),
                "gameFileId", event.fileCopyNaturalId().gameFileId().value(),
                "backupTargetId", event.fileCopyNaturalId().backupTargetId().value(),
                "percentage", event.percentage(),
                "timeLeftMillis", event.timeLeft().toMillis()
        );
    }

    @Override
    public FileCopyReplicationProgressChangedEvent deserialize(Map<String, Object> eventData) {
        return new FileCopyReplicationProgressChangedEvent(
                new FileCopyId((String) eventData.get("fileCopyId")),
                new FileCopyNaturalId(
                        new GameFileId((String) eventData.get("gameFileId")),
                        new BackupTargetId((String) eventData.get("backupTargetId"))
                ),
                (Integer) eventData.get("percentage"),
                Duration.ofMillis((Integer) eventData.get("timeLeftMillis")) // @TODO Should be long but fails
        );
    }

    @Override
    public Class<FileCopyReplicationProgressChangedEvent> getSupportedEventClass() {
        return FileCopyReplicationProgressChangedEvent.class;
    }
}
