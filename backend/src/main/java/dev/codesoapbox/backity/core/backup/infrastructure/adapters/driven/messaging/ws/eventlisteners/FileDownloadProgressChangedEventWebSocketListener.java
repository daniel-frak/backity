package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners;

import dev.codesoapbox.backity.core.backup.domain.events.FileDownloadProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileDownloadProgressUpdatedWsEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileDownloadProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class FileDownloadProgressChangedEventWebSocketListener {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileDownloadProgressUpdatedWsEventMapper wsEventMapper;

    @EventListener
    public void handle(FileDownloadProgressChangedEvent event) {
        FileDownloadProgressUpdatedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_PROGRESS_CHANGED.wsDestination(), payload);
    }
}