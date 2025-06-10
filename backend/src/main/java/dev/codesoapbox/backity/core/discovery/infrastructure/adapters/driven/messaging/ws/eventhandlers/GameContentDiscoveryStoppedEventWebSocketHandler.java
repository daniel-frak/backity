package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStoppedWsEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStoppedWsEventMapper;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameContentDiscoveryStoppedEventWebSocketHandler
        implements DomainEventHandler<GameContentDiscoveryStoppedEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final GameContentDiscoveryStoppedWsEventMapper wsEventMapper;

    @Override
    public Class<GameContentDiscoveryStoppedEvent> getEventClass() {
        return GameContentDiscoveryStoppedEvent.class;
    }

    @Override
    public void handle(GameContentDiscoveryStoppedEvent event) {
        GameContentDiscoveryStoppedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STOPPED.wsDestination(), payload);
    }
}