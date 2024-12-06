package dev.codesoapbox.backity.core.discovery.config;

import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.eventhandlers.FileDiscoveredEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.eventhandlers.FileDiscoveryProgressChangedEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.eventhandlers.FileDiscoveryStatusChangedEventWebSocketHandler;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveredWsEventMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
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
