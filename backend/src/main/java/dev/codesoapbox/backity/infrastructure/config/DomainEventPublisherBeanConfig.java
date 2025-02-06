package dev.codesoapbox.backity.infrastructure.config;

import dev.codesoapbox.backity.infrastructure.adapters.driven.messaging.ws.DomainEventWebSocketPublisher;
import dev.codesoapbox.backity.shared.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Configuration
public class DomainEventPublisherBeanConfig {

    @Bean
    WebSocketEventPublisher webSocketEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        return new WebSocketEventPublisher(simpMessagingTemplate);
    }

    @Bean
    DomainEventPublisher fileBackupEventPublisher(List<DomainEventHandler<?>> domainEventHandlers) {
        @SuppressWarnings("java:S6411") // Lack of Comparable implementation is not a performance concern here
        Map<Class<? extends DomainEvent>, DomainEventHandler<? extends DomainEvent>> handlers =
                domainEventHandlers.stream()
                        .collect(toMap(DomainEventHandler::getEventClass, e -> e));

        return new DomainEventWebSocketPublisher(handlers);
    }
}
