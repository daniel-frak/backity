package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileDownloadProgressUpdatedWsEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileDownloadProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileDownloadProgressChangedEventWebSocketHandler
        implements DomainEventHandler<FileDownloadProgressChangedEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileDownloadProgressUpdatedWsEventMapper wsEventMapper;

    @Override
    public Class<FileDownloadProgressChangedEvent> getEventClass() {
        return FileDownloadProgressChangedEvent.class;
    }

    @Override
    public void handle(FileDownloadProgressChangedEvent event) {
        FileDownloadProgressUpdatedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_PROGRESS_CHANGED.wsDestination(), payload);
    }
}