package dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.FileBackupWebSocketTopics;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileBackupStartedWsEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileBackupStartedWsEventMapper;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEvent;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.messaging.ws.model.FileCopyStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.backup.domain.events.FileBackupStartedEvent;
import dev.codesoapbox.backity.core.filecopy.application.usecases.FileCopyWithContext;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopy;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileBackupStartedEventWebSocketHandler implements DomainEventHandler<FileBackupStartedEvent> {

    private final FileCopyRepository fileCopyRepository;
    private final GameFileRepository gameFileRepository;
    private final GameRepository gameRepository;
    private final WebSocketEventPublisher wsEventPublisher;
    private final FileBackupStartedWsEventMapper backupStartedWsEventMapper;
    private final FileCopyStatusChangedWsEventMapper statusChangedWsEventMapper;

    @Override
    public Class<FileBackupStartedEvent> getEventClass() {
        return FileBackupStartedEvent.class;
    }

    @Override
    public void handle(FileBackupStartedEvent event) {
        FileCopy fileCopy = fileCopyRepository.getById(event.fileCopyId());
        GameFile gameFile = gameFileRepository.getById(event.fileCopyNaturalId().gameFileId());
        Game game = gameRepository.getById(gameFile.getGameId());
        var fileCopyWithContext = new FileCopyWithContext(fileCopy, gameFile, game);
        FileBackupStartedWsEvent backupStartedPayload =
                backupStartedWsEventMapper.toWsEvent(event, fileCopyWithContext);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_STARTED.wsDestination(), backupStartedPayload);

        FileCopyStatusChangedWsEvent payload = statusChangedWsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.wsDestination(), payload);
    }
}