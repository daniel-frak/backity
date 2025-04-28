package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.FileDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.FileDiscoveryStatusChangedWsEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.FileDiscoveryStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
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