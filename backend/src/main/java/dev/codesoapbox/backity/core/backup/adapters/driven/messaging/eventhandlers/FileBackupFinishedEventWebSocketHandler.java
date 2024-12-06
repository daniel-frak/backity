package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.eventhandlers;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedWsEvent;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
import dev.codesoapbox.backity.core.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupFinishedEventWebSocketHandler implements DomainEventHandler<FileBackupFinishedEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileBackupStatusChangedWsEventMapper wsEventMapper;

    @Override
    public Class<FileBackupFinishedEvent> getEventClass() {
        return FileBackupFinishedEvent.class;
    }

    @Override
    public void handle(FileBackupFinishedEvent event) {
        FileBackupStatusChangedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination(), payload);
    }
}