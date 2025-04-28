package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.FileDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.FileDiscoveredWsEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.FileDiscoveredWsEventMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveredEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileDiscoveredEventWebSocketHandler implements DomainEventHandler<FileDiscoveredEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileDiscoveredWsEventMapper wsEventMapper;

    @Override
    public Class<FileDiscoveredEvent> getEventClass() {
        return FileDiscoveredEvent.class;
    }

    @Override
    public void handle(FileDiscoveredEvent event) {
        FileDiscoveredWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileDiscoveryWebSocketTopics.FILE_DISCOVERED.wsDestination(), payload);
    }
}