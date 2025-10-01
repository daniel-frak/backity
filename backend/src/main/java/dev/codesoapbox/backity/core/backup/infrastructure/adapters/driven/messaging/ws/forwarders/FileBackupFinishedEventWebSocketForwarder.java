package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.forwarders;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileBackupFinishedEventExternalForwarder;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupFinishedEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupFinishedEventWebSocketForwarder implements FileBackupFinishedEventExternalForwarder {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileCopyStatusChangedWsEventMapper wsEventMapper;

    @Override
    public void forward(FileBackupFinishedEvent event) {
        FileCopyStatusChangedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination(), payload);
    }
}