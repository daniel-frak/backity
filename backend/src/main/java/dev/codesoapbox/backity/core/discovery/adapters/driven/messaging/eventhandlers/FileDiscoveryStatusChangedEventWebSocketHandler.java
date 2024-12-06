package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.eventhandlers;

import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.FileDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryStatusChangedWsEvent;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
import dev.codesoapbox.backity.core.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileDiscoveryStatusChangedEventWebSocketHandler
        implements DomainEventHandler<FileDiscoveryStatusChangedEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileDiscoveryStatusChangedWsEventMapper wsEventMapper;

    @Override
    public Class<FileDiscoveryStatusChangedEvent> getEventClass() {
        return FileDiscoveryStatusChangedEvent.class;
    }

    @Override
    public void handle(FileDiscoveryStatusChangedEvent event) {
        FileDiscoveryStatusChangedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileDiscoveryWebSocketTopics.FILE_DISCOVERY_STATUS_CHANGED.wsDestination(), payload);
    }
}