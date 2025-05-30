package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameFileDiscoveredEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameFileDiscoveredWsEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameFileDiscoveredWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameFileDiscoveredEventWebSocketHandler implements DomainEventHandler<GameFileDiscoveredEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final GameFileDiscoveredWsEventMapper wsEventMapper;

    @Override
    public Class<GameFileDiscoveredEvent> getEventClass() {
        return GameFileDiscoveredEvent.class;
    }

    @Override
    public void handle(GameFileDiscoveredEvent event) {
        GameFileDiscoveredWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(GameContentDiscoveryWebSocketTopics.FILE_DISCOVERED.wsDestination(), payload);
    }
}