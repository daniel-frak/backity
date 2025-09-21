package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.SpringWebSocketEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class WebSocketEventPublisherBeanConfig {

    @Bean
    SpringWebSocketEventPublisher webSocketEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        return new SpringWebSocketEventPublisher(simpMessagingTemplate);
    }
}
