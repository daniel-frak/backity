package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.DomainEventWebSocketPublisher;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

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
