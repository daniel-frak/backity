package dev.codesoapbox.backity.core.backup.adapters.driven.messaging;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.*;
import dev.codesoapbox.backity.core.backup.domain.FileBackupEventPublisher;
import dev.codesoapbox.backity.core.backup.domain.FileBackupProgress;
import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class FileBackupEventWebSocketPublisher implements FileBackupEventPublisher {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileBackupStartedWsEventMapper fileBackupStartedWsEventMapper;
    private final FileBackupProgressUpdatedWsEventMapper fileBackupProgressUpdatedWsEventMapper;
    private final FileBackupStatusChangedWsEventMapper fileBackupStatusChangedWsEventMapper;

    @Override
    public void publishBackupStartedEvent(GameFile gameFile) {
        FileBackupStartedWsEvent payload = fileBackupStartedWsEventMapper.toWsEvent(gameFile);
        publish(FileBackupWebSocketTopics.BACKUP_STARTED.toString(), payload);
    }

    @Override
    public void publishFileBackupProgressChangedEvent(FileBackupProgress fileBackupProgress) {
        FileBackupProgressUpdatedWsEvent payload = fileBackupProgressUpdatedWsEventMapper.toWsEvent(fileBackupProgress);
        publish(FileBackupWebSocketTopics.BACKUP_PROGRESS_UPDATE.toString(), payload);
    }

    private void publish(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }

    @Override
    public void publishBackupFinishedEvent(GameFile gameFile) {
        FileBackupStatusChangedWsEvent payload = fileBackupStatusChangedWsEventMapper.toWsEvent(gameFile);
        publish(FileBackupWebSocketTopics.BACKUP_STATUS_CHANGED.toString(), payload);
    }
}