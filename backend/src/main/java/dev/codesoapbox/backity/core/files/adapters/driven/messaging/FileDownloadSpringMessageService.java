package dev.codesoapbox.backity.core.files.adapters.driven.messaging;

import dev.codesoapbox.backity.core.files.domain.downloading.model.GameFileVersion;
import dev.codesoapbox.backity.core.files.domain.downloading.model.messages.FileDownloadProgress;
import dev.codesoapbox.backity.core.files.domain.downloading.services.FileDownloadMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class FileDownloadSpringMessageService implements FileDownloadMessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendDownloadStarted(GameFileVersion payload) {
        sendMessage(FileDownloadMessageTopics.DOWNLOAD_STARTED.toString(), payload);
    }

    @Override
    public void sendProgress(FileDownloadProgress payload) {
        sendMessage(FileDownloadMessageTopics.DOWNLOAD_PROGRESS.toString(), payload);
    }

    private void sendMessage(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }

    @Override
    public void sendDownloadFinished(GameFileVersion payload) {
        sendMessage(FileDownloadMessageTopics.DOWNLOAD_FINISHED.toString(), payload);
    }
}