package dev.codesoapbox.backity.core.backup.adapters.driven.messaging.eventhandlers;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupProgressUpdatedWsEvent;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupProgressChangedEvent;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
import dev.codesoapbox.backity.core.shared.domain.DomainEventHandler;
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