package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers.GameContentDiscoveryProgressChangedEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers.GameContentDiscoveryStatusChangedEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers.GameFileDiscoveredEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameContentDiscoveryStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.model.GameFileDiscoveredWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameContentDiscoveryWebSocketBeanConfig {

    @Bean
    GameContentDiscoveryProgressChangedEventWebSocketHandler gameContentDiscoveryProgressChangedWsEventMapper(
            WebSocketEventPublisher wsEventPublisher) {
        GameContentDiscoveryProgressChangedWsEventMapper wsEventMapper =
                Mappers.getMapper(GameContentDiscoveryProgressChangedWsEventMapper.class);
        return new GameContentDiscoveryProgressChangedEventWebSocketHandler(wsEventPublisher, wsEventMapper);
    }

    @Bean
    GameContentDiscoveryStatusChangedEventWebSocketHandler gameContentDiscoveryStatusChangedEventWebSocketHandler(
            WebSocketEventPublisher wsEventPublisher) {
        GameContentDiscoveryStatusChangedWsEventMapper wsEventMapper =
                Mappers.getMapper(GameContentDiscoveryStatusChangedWsEventMapper.class);
        return new GameContentDiscoveryStatusChangedEventWebSocketHandler(wsEventPublisher, wsEventMapper);
    }

    @Bean
    GameFileDiscoveredEventWebSocketHandler gameFileDiscoveredEventWebSocketHandler(
            WebSocketEventPublisher wsEventPublisher) {
        GameFileDiscoveredWsEventMapper wsEventMapper = Mappers.getMapper(GameFileDiscoveredWsEventMapper.class);
        return new GameFileDiscoveredEventWebSocketHandler(wsEventPublisher, wsEventMapper);
    }
}
