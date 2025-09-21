package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventlisteners;

import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFailedEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.SpringWebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class FileBackupFailedEventSpringWebSocketListener {

    private final SpringWebSocketEventPublisher wsEventPublisher;
    private final FileCopyStatusChangedWsEventMapper wsEventMapper;

    @EventListener
    public void handle(FileBackupFailedEvent event) {
        FileCopyStatusChangedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination(), payload);
    }
}