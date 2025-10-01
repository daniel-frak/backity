package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.forwarders;

import dev.codesoapbox.backity.core.discovery.application.eventhandlers.GameContentDiscoveryStartedEventExternalForwarder;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStartedWsEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStartedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameContentDiscoveryStartedEventWebSocketForwarder
        implements GameContentDiscoveryStartedEventExternalForwarder {

    private final WebSocketEventPublisher wsEventPublisher;
    private final GameContentDiscoveryStartedWsEventMapper wsEventMapper;

    @Override
    public void forward(GameContentDiscoveryStartedEvent event) {
        GameContentDiscoveryStartedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STARTED.wsDestination(), payload);
    }
}