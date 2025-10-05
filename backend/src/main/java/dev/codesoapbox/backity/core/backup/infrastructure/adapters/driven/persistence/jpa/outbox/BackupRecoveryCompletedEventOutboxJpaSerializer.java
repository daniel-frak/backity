package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.core.backup.domain.events.BackupRecoveryCompletedEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxJpaSerializer;

import java.util.Map;

// @TODO Test
public class BackupRecoveryCompletedEventOutboxJpaSerializer
        implements DomainEventOutboxJpaSerializer<BackupRecoveryCompletedEvent> {

    @Override
    public Map<String, Object> serialize(BackupRecoveryCompletedEvent event) {
        return Map.of();
    }

    @Override
    public BackupRecoveryCompletedEvent deserialize(Map<String, Object> eventData) {
        return new BackupRecoveryCompletedEvent();
    }

    @Override
    public Class<BackupRecoveryCompletedEvent> getSupportedEventClass() {
        return BackupRecoveryCompletedEvent.class;
    }
}
