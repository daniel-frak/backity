package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileBackupStartedWsEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileBackupStartedWsEventMapper;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupStartedEventWebSocketHandler implements DomainEventHandler<FileBackupStartedEvent> {

    private final GameFileRepository gameFileRepository;
    private final WebSocketEventPublisher wsEventPublisher;
    private final FileBackupStartedWsEventMapper backupStartedWsEventMapper;
    private final FileCopyStatusChangedWsEventMapper statusChangedWsEventMapper;

    @Override
    public Class<FileBackupStartedEvent> getEventClass() {
        return FileBackupStartedEvent.class;
    }

    @Override
    public void handle(FileBackupStartedEvent event) {
        GameFile gameFile = gameFileRepository.getById(event.fileCopyNaturalId().gameFileId());
        FileBackupStartedWsEvent backupStartedPayload = backupStartedWsEventMapper.toWsEvent(event, gameFile);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_STARTED.wsDestination(), backupStartedPayload);

        FileCopyStatusChangedWsEvent payload = statusChangedWsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination(), payload);
    }
}