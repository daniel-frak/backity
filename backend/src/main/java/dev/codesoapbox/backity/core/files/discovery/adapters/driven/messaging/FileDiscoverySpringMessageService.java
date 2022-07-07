package dev.codesoapbox.backity.core.files.discovery.adapters.driven.messaging;

import dev.codesoapbox.backity.core.files.discovery.domain.model.DiscoveredFile;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.discovery.domain.model.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.files.discovery.domain.services.FileDiscoveryMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class FileDiscoverySpringMessageService implements FileDiscoveryMessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void sendStatus(FileDiscoveryStatus payload) {
        sendMessage(FileDiscoveryMessageTopics.FILE_DISCOVERY_STATUS.toString(), payload);
    }

    private void sendMessage(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }

    @Override
    public void sendProgress(FileDiscoveryProgress payload) {
        sendMessage(FileDiscoveryMessageTopics.FILE_DISCOVERY_PROGRESS.toString(), payload);
    }

    @Override
    public void sendDiscoveredFile(DiscoveredFile payload) {
        sendMessage(FileDiscoveryMessageTopics.FILE_DISCOVERY.toString(), payload);
    }
}
