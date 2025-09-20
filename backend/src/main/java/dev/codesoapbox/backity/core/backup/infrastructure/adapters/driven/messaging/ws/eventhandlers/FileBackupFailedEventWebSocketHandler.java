package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class FileBackupFailedEventWebSocketHandler {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileCopyStatusChangedWsEventMapper fileCopyStatusChangedWsEventMapper;

    @EventListener
    public void handle(FileBackupFailedEvent event) {
        FileCopyStatusChangedWsEvent payload = fileCopyStatusChangedWsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination(), payload);
    }
}