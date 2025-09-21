package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventlisteners;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStoppedWsEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStoppedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.SpringWebSocketEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class GameContentDiscoveryStoppedEventSpringWebSocketListener {

    private final SpringWebSocketEventPublisher wsEventPublisher;
    private final GameContentDiscoveryStoppedWsEventMapper wsEventMapper;

    @EventListener
    public void handle(GameContentDiscoveryStoppedEvent event) {
        GameContentDiscoveryStoppedWsEvent payload = wsEventMapper.toWsEvent(event);
        wsEventPublisher.publish(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STOPPED.wsDestination(), payload);
    }
}