package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStartedWsEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStartedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameContentDiscoveryStartedEventWebSocketHandler
        implements DomainEventHandler<GameContentDiscoveryStartedEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final GameContentDiscoveryStartedWsEventMapper wsEventMapper;

    @Override
    public Class<GameContentDiscoveryStartedEvent> getEventClass() {
        return GameContentDiscoveryStartedEvent.class;
    }

    @Override
    public void handle(GameContentDiscoveryStartedEvent event) {
        GameContentDiscoveryStartedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STARTED.wsDestination(), payload);
    }
}