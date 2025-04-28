package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileBackupStatusChangedWsEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileBackupStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupFailedEventWebSocketHandler implements DomainEventHandler<FileBackupFailedEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileBackupStatusChangedWsEventMapper fileBackupStatusChangedWsEventMapper;

    @Override
    public Class<FileBackupFailedEvent> getEventClass() {
        return FileBackupFailedEvent.class;
    }

    @Override
    public void handle(FileBackupFailedEvent event) {
        FileBackupStatusChangedWsEvent payload = fileBackupStatusChangedWsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination(), payload);
    }
}