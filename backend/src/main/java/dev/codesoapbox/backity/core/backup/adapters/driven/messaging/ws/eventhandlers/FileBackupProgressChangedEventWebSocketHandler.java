package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws.model.FileBackupProgressUpdatedWsEvent;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.ws.model.FileBackupProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupProgressChangedEvent;
import dev.codesoapbox.backity.shared.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupProgressChangedEventWebSocketHandler
        implements DomainEventHandler<FileBackupProgressChangedEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileBackupProgressUpdatedWsEventMapper wsEventMapper;

    @Override
    public Class<FileBackupProgressChangedEvent> getEventClass() {
        return FileBackupProgressChangedEvent.class;
    }

    @Override
    public void handle(FileBackupProgressChangedEvent event) {
        FileBackupProgressUpdatedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_PROGRESS_CHANGED.wsDestination(), payload);
    }
}