package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.eventhandlers;

import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.FileDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveredWsEvent;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveredWsEventMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveredEvent;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
import dev.codesoapbox.backity.core.shared.domain.DomainEventHandler;
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