package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.WebSocketEventPublisherBeanConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@WebSocketEventPublisherBeanConfiguration
public class WebSocketEventPublisherBeanConfig {

    @Bean
    WebSocketEventPublisher webSocketEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        return new WebSocketEventPublisher(simpMessagingTemplate);
    }
}
