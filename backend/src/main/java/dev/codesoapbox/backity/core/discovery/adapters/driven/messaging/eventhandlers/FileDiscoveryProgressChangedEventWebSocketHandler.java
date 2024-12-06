package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.eventhandlers;

import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.FileDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryProgressChangedWsEvent;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
import dev.codesoapbox.backity.core.shared.domain.DomainEventHandler;
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