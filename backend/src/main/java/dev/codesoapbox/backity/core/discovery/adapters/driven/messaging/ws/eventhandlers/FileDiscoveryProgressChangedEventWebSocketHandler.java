package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.FileDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.model.FileDiscoveryProgressChangedWsEvent;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.model.FileDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.shared.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FileDiscoveryProgressChangedEventWebSocketHandler
        implements DomainEventHandler<FileDiscoveryProgressChangedEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final FileDiscoveryProgressChangedWsEventMapper wsEventMapper;

    @Override
    public Class<FileDiscoveryProgressChangedEvent> getEventClass() {
        return FileDiscoveryProgressChangedEvent.class;
    }

    @Override
    public void handle(FileDiscoveryProgressChangedEvent event) {
        FileDiscoveryProgressChangedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(FileDiscoveryWebSocketTopics.FILE_DISCOVERY_PROGRESS_CHANGED.wsDestination(), payload);
    }
}