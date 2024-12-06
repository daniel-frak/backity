package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.eventhandlers;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedWsEvent;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
import dev.codesoapbox.backity.core.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupFailedEventWebSocketHandler implements DomainEventHandler<FileBackupFailedEvent> {

    private final WebSocketEventPublisher webSocketEventPublisher;
    private final FileBackupStatusChangedWsEventMapper fileBackupStatusChangedWsEventMapper;

    @Override
    public Class<FileBackupFailedEvent> getEventClass() {
        return FileBackupFailedEvent.class;
    }

    @Override
    public void handle(FileBackupFailedEvent event) {
        FileBackupStatusChangedWsEvent payload = fileBackupStatusChangedWsEventMapper.toWsEvent(event);
        webSocketEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination(), payload);
    }
}