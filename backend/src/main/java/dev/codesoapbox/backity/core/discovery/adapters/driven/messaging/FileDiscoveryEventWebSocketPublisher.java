package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging;

import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.*;
import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryEventPublisher;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
@RequiredArgsConstructor
public class FileDiscoveryEventWebSocketPublisher implements FileDiscoveryEventPublisher {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final FileDiscoveredWsEventMapper fileDiscoveredWsEventMapper;
    private final FileDiscoveryStatusChangedWsEventMapper fileDiscoveryStatusChangedWsEventMapper;
    private final FileDiscoveryProgressChangedWsEventMapper fileDiscoveryProgressChangedWsEventMapper;

    @Override
    public void publishStatusChangedEvent(FileDiscoveryStatusChangedEvent status) {
        FileDiscoveryStatusChangedWsEvent payload = fileDiscoveryStatusChangedWsEventMapper.toWsEvent(status);
        publish(FileDiscoveryWebSocketTopics.FILE_DISCOVERY_STATUS_CHANGED.toString(), payload);
    }

    private void publish(String topic, Object payload) {
        log.debug("Sending payload ({}) to topic {}", payload, topic);
        simpMessagingTemplate.convertAndSend(topic, payload);
    }

    @Override
    public void publishProgressChangedEvent(FileDiscoveryProgressChangedEvent progress) {
        FileDiscoveryProgressChangedWsEvent payload = fileDiscoveryProgressChangedWsEventMapper.toWsEvent(progress);
        publish(FileDiscoveryWebSocketTopics.FILE_DISCOVERY_PROGRESS_UPDATE.toString(), payload);
    }

    @Override
    public void publishFileDiscoveredEvent(FileDetails fileDetails) {
        FileDiscoveredWsEvent payload = fileDiscoveredWsEventMapper.toWsEvent(fileDetails.getSourceFileDetails());
        publish(FileDiscoveryWebSocketTopics.FILE_DISCOVERED.toString(), payload);
    }
}
