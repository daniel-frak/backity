package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.DomainEventWebSocketPublisher;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
@Import(DomainEventPublisherPostConfig.class)
public class DomainEventPublisherBeanConfig {

    @Bean
    WebSocketEventPublisher webSocketEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        return new WebSocketEventPublisher(simpMessagingTemplate);
    }

    @Bean
    DomainEventWebSocketPublisher domainEventWebSocketPublisher() {
        return new DomainEventWebSocketPublisher();
    }
}
