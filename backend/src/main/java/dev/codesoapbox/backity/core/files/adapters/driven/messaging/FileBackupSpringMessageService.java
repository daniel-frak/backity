package dev.codesoapbox.backity.core.files.adapters.driven.messaging;

import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.backup.model.messages.FileBackupProgress;
import dev.codesoapbox.backity.core.files.domain.backup.services.FileBackupMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class FileBackupSpringMessageService implements FileBackupMessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendBackupStarted(GameFileVersion payload) {
        sendMessage(FileBackupMessageTopics.DOWNLOAD_STARTED.toString(), payload);
    }

    @Override
    public void sendProgress(FileBackupProgress payload) {
        sendMessage(FileBackupMessageTopics.DOWNLOAD_PROGRESS.toString(), payload);
    }

    private void sendMessage(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }

    @Override
    public void sendBackupFinished(GameFileVersion payload) {
        sendMessage(FileBackupMessageTopics.DOWNLOAD_FINISHED.toString(), payload);
    }
}