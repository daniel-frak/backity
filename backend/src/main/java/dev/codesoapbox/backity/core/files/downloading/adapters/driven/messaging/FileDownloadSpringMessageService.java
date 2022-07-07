package dev.codesoapbox.backity.core.files.downloading.adapters.driven.messaging;

import dev.codesoapbox.backity.core.files.downloading.domain.model.EnqueuedFileDownload;
import dev.codesoapbox.backity.core.files.downloading.domain.services.FileDownloadMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class FileDownloadSpringMessageService implements FileDownloadMessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendDownloadStarted(EnqueuedFileDownload payload) {
        sendMessage(FileDownloadMessageTopics.DOWNLOAD_STARTED.toString(), payload);
    }

    private void sendMessage(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }

    @Override
    public void sendDownloadFinished(EnqueuedFileDownload payload) {
        sendMessage(FileDownloadMessageTopics.DOWNLOAD_FINISHED.toString(), payload);
    }
}