package dev.codesoapbox.backity.core.discovery.config;

import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.eventhandlers.FileDiscoveredEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.eventhandlers.FileDiscoveryProgressChangedEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.eventhandlers.FileDiscoveryStatusChangedEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.model.FileDiscoveredWsEventMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.model.FileDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.model.FileDiscoveryStatusChangedWsEventMapper;
import dev.codesoapbox.backity.shared.adapters.driven.messaging.ws.WebSocketEventPublisher;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileDiscoveryWebSocketBeanConfig {

    @Bean
    FileDiscoveredEventWebSocketHandler fileDiscoveredEventWebSocketHandler(WebSocketEventPublisher wsEventPublisher) {
        FileDiscoveredWsEventMapper wsEventMapper = Mappers.getMapper(FileDiscoveredWsEventMapper.class);
        return new FileDiscoveredEventWebSocketHandler(wsEventPublisher, wsEventMapper);
    }

    @Bean
    FileDiscoveryProgressChangedEventWebSocketHandler fileDiscoveryProgressChangedWsEventMapper(
            WebSocketEventPublisher wsEventPublisher) {
        FileDiscoveryProgressChangedWsEventMapper wsEventMapper =
                Mappers.getMapper(FileDiscoveryProgressChangedWsEventMapper.class);
        return new FileDiscoveryProgressChangedEventWebSocketHandler(wsEventPublisher, wsEventMapper);
    }

    @Bean
    FileDiscoveryStatusChangedEventWebSocketHandler fileDiscoveryStatusChangedEventWebSocketHandler(
            WebSocketEventPublisher wsEventPublisher) {
        FileDiscoveryStatusChangedWsEventMapper wsEventMapper =
                Mappers.getMapper(FileDiscoveryStatusChangedWsEventMapper.class);
        return new FileDiscoveryStatusChangedEventWebSocketHandler(wsEventPublisher, wsEventMapper);
    }
}
