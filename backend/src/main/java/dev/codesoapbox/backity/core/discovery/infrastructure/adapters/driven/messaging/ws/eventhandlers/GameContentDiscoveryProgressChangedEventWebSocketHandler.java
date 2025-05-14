package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryProgressChangedWsEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameContentDiscoveryProgressChangedEventWebSocketHandler
        implements DomainEventHandler<GameContentDiscoveryProgressChangedEvent> {

    private final WebSocketEventPublisher wsEventPublisher;
    private final GameContentDiscoveryProgressChangedWsEventMapper wsEventMapper;

    @Override
    public Class<GameContentDiscoveryProgressChangedEvent> getEventClass() {
        return GameContentDiscoveryProgressChangedEvent.class;
    }

    @Override
    public void handle(GameContentDiscoveryProgressChangedEvent event) {
        GameContentDiscoveryProgressChangedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_PROGRESS_CHANGED.wsDestination(), payload);
    }
}