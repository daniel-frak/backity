package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.eventhandlers;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStartedWsEvent;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStartedWsEventMapper;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
import dev.codesoapbox.backity.core.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupStartedEventWebSocketHandler implements DomainEventHandler<FileBackupStartedEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileBackupStartedWsEventMapper wsEventMapper;

    @Override
    public Class<FileBackupStartedEvent> getEventClass() {
        return FileBackupStartedEvent.class;
    }

    @Override
    public void handle(FileBackupStartedEvent event) {
        FileBackupStartedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_STARTED.wsDestination(), payload);
    }
}