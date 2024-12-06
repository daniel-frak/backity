package dev.codesoapbox.backity.core.shared.config;

import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.DomainEventWebSocketPublisher;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
import dev.codesoapbox.backity.core.shared.domain.DomainEvent;
import dev.codesoapbox.backity.core.shared.domain.DomainEventHandler;
import dev.codesoapbox.backity.core.shared.domain.DomainEventPublisher;
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
        Map<Class<? extends DomainEvent>, DomainEventHandler<? extends DomainEvent>> handlers =
                domainEventHandlers.stream()
                        .collect(toMap(DomainEventHandler::getEventClass, e -> e));

        return new DomainEventWebSocketPublisher(handlers);
    }
}
