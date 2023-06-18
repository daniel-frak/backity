package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging;

import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.*;
import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryMessageService;
import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryProgress;
import dev.codesoapbox.backity.core.discovery.domain.messages.FileDiscoveryStatus;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class FileDiscoverySpringMessageService implements FileDiscoveryMessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileDiscoveredMessageMapper fileDiscoveredMessageMapper;
    private final FileDiscoveryStatusChangedMessageMapper fileDiscoveryStatusChangedMessageMapper;
    private final FileDiscoveryProgressUpdateMessageMapper fileDiscoveryProgressUpdateMessageMapper;

    @Override
    public void sendStatusChangedMessage(FileDiscoveryStatus status) {
        FileDiscoveryStatusChangedWsMessage message = fileDiscoveryStatusChangedMessageMapper.toMessage(status);
        sendMessage(FileDiscoveryMessageTopics.FILE_DISCOVERY_STATUS_CHANGED.toString(), message);
    }

    private void sendMessage(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }

    @Override
    public void sendProgressUpdateMessage(FileDiscoveryProgress progress) {
        var message = fileDiscoveryProgressUpdateMessageMapper.toMessage(progress);
        sendMessage(FileDiscoveryMessageTopics.FILE_DISCOVERY_PROGRESS_UPDATE.toString(), message);
    }

    @Override
    public void sendFileDiscoveredMessage(GameFileDetails gameFileDetails) {
        FileDiscoveredWsMessage payload = fileDiscoveredMessageMapper.toMessage(gameFileDetails.getSourceFileDetails());
        sendMessage(FileDiscoveryMessageTopics.FILE_DISCOVERED.toString(), payload);
    }
}
