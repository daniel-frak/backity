package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventlisteners.GameContentDiscoveryProgressChangedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventlisteners.GameContentDiscoveryStartedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventlisteners.GameContentDiscoveryStoppedEventSpringWebSocketListener;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStartedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStoppedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameContentDiscoverySpringWebSocketListenerBeanConfig {

    @Bean
    GameContentDiscoveryProgressChangedEventSpringWebSocketListener
    gameContentDiscoveryProgressChangedEventSpringWebSocketListener(
            WebSocketEventPublisher wsEventPublisher) {
        GameContentDiscoveryProgressChangedWsEventMapper wsEventMapper =
                Mappers.getMapper(GameContentDiscoveryProgressChangedWsEventMapper.class);
        return new GameContentDiscoveryProgressChangedEventSpringWebSocketListener(wsEventPublisher, wsEventMapper);
    }

    @Bean
    GameContentDiscoveryStartedEventSpringWebSocketListener gameContentDiscoveryStartedEventSpringWebSocketListener(
            WebSocketEventPublisher wsEventPublisher) {
        GameContentDiscoveryStartedWsEventMapper wsEventMapper =
                Mappers.getMapper(GameContentDiscoveryStartedWsEventMapper.class);
        return new GameContentDiscoveryStartedEventSpringWebSocketListener(wsEventPublisher, wsEventMapper);
    }

    @Bean
    GameContentDiscoveryStoppedEventSpringWebSocketListener gameContentDiscoveryStoppedEventSpringWebSocketListener(
            WebSocketEventPublisher wsEventPublisher) {
        GameContentDiscoveryStoppedWsEventMapper wsEventMapper =
                Mappers.getMapper(GameContentDiscoveryStoppedWsEventMapper.class);
        return new GameContentDiscoveryStoppedEventSpringWebSocketListener(wsEventPublisher, wsEventMapper);
    }
}
