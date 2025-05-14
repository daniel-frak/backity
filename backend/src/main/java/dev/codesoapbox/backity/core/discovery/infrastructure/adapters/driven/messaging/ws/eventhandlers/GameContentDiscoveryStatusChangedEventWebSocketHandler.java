package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStatusChangedWsEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameContentDiscoveryStatusChangedEventWebSocketHandler
        implements DomainEventHandler<GameContentDiscoveryStatusChangedEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final GameContentDiscoveryStatusChangedWsEventMapper wsEventMapper;

    @Override
    public Class<GameContentDiscoveryStatusChangedEvent> getEventClass() {
        return GameContentDiscoveryStatusChangedEvent.class;
    }

    @Override
    public void handle(GameContentDiscoveryStatusChangedEvent event) {
        GameContentDiscoveryStatusChangedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STATUS_CHANGED.wsDestination(), payload);
    }
}