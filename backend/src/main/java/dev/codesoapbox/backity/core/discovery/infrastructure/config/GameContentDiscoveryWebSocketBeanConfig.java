package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers.GameContentDiscoveryProgressChangedEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers.GameContentDiscoveryStartedEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers.GameContentDiscoveryStoppedEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStartedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStoppedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameContentDiscoveryWebSocketBeanConfig {

    @Bean
    GameContentDiscoveryProgressChangedEventWebSocketHandler gameContentDiscoveryProgressChangedEventWebSocketHandler(
            WebSocketEventPublisher wsEventPublisher) {
        GameContentDiscoveryProgressChangedWsEventMapper wsEventMapper =
                Mappers.getMapper(GameContentDiscoveryProgressChangedWsEventMapper.class);
        return new GameContentDiscoveryProgressChangedEventWebSocketHandler(wsEventPublisher, wsEventMapper);
    }

    @Bean
    GameContentDiscoveryStartedEventWebSocketHandler gameContentDiscoveryStartedEventWebSocketHandler(
            WebSocketEventPublisher wsEventPublisher) {
        GameContentDiscoveryStartedWsEventMapper wsEventMapper =
                Mappers.getMapper(GameContentDiscoveryStartedWsEventMapper.class);
        return new GameContentDiscoveryStartedEventWebSocketHandler(wsEventPublisher, wsEventMapper);
    }

    @Bean
    GameContentDiscoveryStoppedEventWebSocketHandler gameContentDiscoveryStoppedEventWebSocketHandler(
            WebSocketEventPublisher wsEventPublisher) {
        GameContentDiscoveryStoppedWsEventMapper wsEventMapper =
                Mappers.getMapper(GameContentDiscoveryStoppedWsEventMapper.class);
        return new GameContentDiscoveryStoppedEventWebSocketHandler(wsEventPublisher, wsEventMapper);
    }
}
