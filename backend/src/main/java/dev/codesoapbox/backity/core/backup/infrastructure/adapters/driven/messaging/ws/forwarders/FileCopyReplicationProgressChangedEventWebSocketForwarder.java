package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.forwarders;

import dev.codesoapbox.backity.core.backup.application.eventhandlers.FileCopyReplicationProgressChangedEventExternalForwarder;
import dev.codesoapbox.backity.core.backup.domain.events.FileCopyReplicationProgressChangedEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyReplicationProgressUpdatedWsEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyReplicationProgressUpdatedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileCopyReplicationProgressChangedEventWebSocketForwarder
        implements FileCopyReplicationProgressChangedEventExternalForwarder {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileCopyReplicationProgressUpdatedWsEventMapper wsEventMapper;

    @Override
    public void forward(FileCopyReplicationProgressChangedEvent event) {
        FileCopyReplicationProgressUpdatedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_PROGRESS_CHANGED.wsDestination(), payload);
    }
}