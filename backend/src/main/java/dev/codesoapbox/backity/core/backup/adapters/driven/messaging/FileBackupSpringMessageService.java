package dev.codesoapbox.backity.core.backup.adapters.driven.messaging;

import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStartedMessage;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStartedMessageMapper;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedMessage;
import dev.codesoapbox.backity.core.backup.adapters.driven.messaging.model.FileBackupStatusChangedMessageMapper;
import dev.codesoapbox.backity.core.backup.domain.FileBackupMessageService;
import dev.codesoapbox.backity.core.backup.domain.FileBackupProgress;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class FileBackupSpringMessageService implements FileBackupMessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileBackupStartedMessageMapper fileBackupStartedMessageMapper;
    private final FileBackupStatusChangedMessageMapper fileBackupStatusChangedMessageMapper;

    @Override
    public void sendBackupStarted(GameFileDetails gameFileDetails) {
        FileBackupStartedMessage payload = fileBackupStartedMessageMapper.toMessage(gameFileDetails);
        sendMessage(FileBackupMessageTopics.BACKUP_STARTED.toString(), payload);
    }

    @Override
    public void sendProgress(FileBackupProgress payload) {
        sendMessage(FileBackupMessageTopics.BACKUP_PROGRESS_UPDATE.toString(), payload);
    }

    private void sendMessage(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }

    @Override
    public void sendBackupFinished(GameFileDetails gameFileDetails) {
        FileBackupStatusChangedMessage payload = fileBackupStatusChangedMessageMapper.toMessage(gameFileDetails);
        sendMessage(FileBackupMessageTopics.BACKUP_STATUS_CHANGED.toString(), payload);
    }
}