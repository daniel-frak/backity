package dev.codesoapbox.backity.core.discovery.infrastructure.config.events;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.forwarders.GameContentDiscoveryProgressChangedEventWebSocketForwarder;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.forwarders.GameContentDiscoveryStartedEventWebSocketForwarder;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.forwarders.GameContentDiscoveryStoppedEventWebSocketForwarder;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStartedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStoppedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.WebSocketEventForwarderBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@WebSocketEventForwarderBeanConfiguration
public class GameContentDiscoveryWebSocketForwarderBeanConfig {

    @Bean
    GameContentDiscoveryStartedEventWebSocketForwarder gameContentDiscoveryStartedEventWebSocketForwarder(
            WebSocketEventPublisher wsEventPublisher) {
        GameContentDiscoveryStartedWsEventMapper wsEventMapper =
                Mappers.getMapper(GameContentDiscoveryStartedWsEventMapper.class);
        return new GameContentDiscoveryStartedEventWebSocketForwarder(wsEventPublisher, wsEventMapper);
    }

    @Bean
    GameContentDiscoveryProgressChangedEventWebSocketForwarder
    gameContentDiscoveryProgressChangedEventWebSocketForwarder(WebSocketEventPublisher wsEventPublisher) {
        GameContentDiscoveryProgressChangedWsEventMapper wsEventMapper =
                Mappers.getMapper(GameContentDiscoveryProgressChangedWsEventMapper.class);
        return new GameContentDiscoveryProgressChangedEventWebSocketForwarder(wsEventPublisher, wsEventMapper);
    }

    @Bean
    GameContentDiscoveryStoppedEventWebSocketForwarder gameContentDiscoveryStoppedEventWebSocketForwarder(
            WebSocketEventPublisher wsEventPublisher) {
        GameContentDiscoveryStoppedWsEventMapper wsEventMapper =
                Mappers.getMapper(GameContentDiscoveryStoppedWsEventMapper.class);
        return new GameContentDiscoveryStoppedEventWebSocketForwarder(wsEventPublisher, wsEventMapper);
    }
}
