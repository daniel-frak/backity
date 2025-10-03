package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.forwarders;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryProgressChangedWsEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwarder;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import lombok.RequiredArgsConstructor;

@DomainEventForwarder
@RequiredArgsConstructor
public class GameContentDiscoveryProgressChangedEventWebSocketForwarder {

    private final WebSocketEventPublisher wsEventPublisher;
    private final GameContentDiscoveryProgressChangedWsEventMapper wsEventMapper;

    public void forward(GameContentDiscoveryProgressChangedEvent event) {
        GameContentDiscoveryProgressChangedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_PROGRESS_CHANGED.wsDestination(), payload);
    }
}
