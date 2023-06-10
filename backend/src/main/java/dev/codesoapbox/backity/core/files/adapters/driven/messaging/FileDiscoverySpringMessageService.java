package dev.codesoapbox.backity.core.files.adapters.driven.messaging;

import dev.codesoapbox.backity.core.files.adapters.driven.messaging.model.GameFileDetailsMessage;
import dev.codesoapbox.backity.core.files.adapters.driven.messaging.model.GameFileDetailsMessageMapper;
import dev.codesoapbox.backity.core.files.domain.backup.model.GameFileDetails;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.files.domain.discovery.model.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.files.domain.discovery.services.FileDiscoveryMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class FileDiscoverySpringMessageService implements FileDiscoveryMessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final GameFileDetailsMessageMapper gameFileDetailsMessageMapper;

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
    public void sendDiscoveredFile(GameFileDetails gameFileDetails) {
        GameFileDetailsMessage payload = gameFileDetailsMessageMapper.toMessage(gameFileDetails);
        sendMessage(FileDiscoveryMessageTopics.FILE_DISCOVERY.toString(), payload);
    }
}
