package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.forwarders;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStoppedWsEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStoppedWsEventMapper;
import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwarder;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;

@DomainEventForwarder
@RequiredArgsConstructor
public class GameContentDiscoveryStoppedEventWebSocketForwarder {

    private final WebSocketEventPublisher wsEventPublisher;
    private final GameContentDiscoveryStoppedWsEventMapper wsEventMapper;

    public void forward(GameContentDiscoveryStoppedEvent event) {
        GameContentDiscoveryStoppedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STOPPED.wsDestination(), payload);
    }
}