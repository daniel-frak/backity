package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.messaging.ws.CustomWebSocketMessageBrokerConfigurer;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.WebSocketEventPublisherSliceConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import tools.jackson.databind.json.JsonMapper;

@WebSocketEventPublisherSliceConfiguration
public class WebSocketEventPublisherBeanConfig {

    @Bean
    WebSocketEventPublisher webSocketEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        return new WebSocketEventPublisher(simpMessagingTemplate);
    }

    @Bean
    CustomWebSocketMessageBrokerConfigurer customWebSocketMessageBrokerConfigurer(JsonMapper jsonMapper) {
        return new CustomWebSocketMessageBrokerConfigurer(jsonMapper);
    }
}
