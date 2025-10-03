package dev.codesoapbox.backity.shared.infrastructure.config.events;

import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.eventlisteners.spring.DomainEventForwardingSpringListener;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringEventListenerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@SpringEventListenerBeanConfiguration
public class DomainEventSpringListenerBeanConfig {

    @Bean
    DomainEventForwardingSpringListener domainEventForwardingSpringListener(DomainEventForwardingHandler eventHandler) {
        return new DomainEventForwardingSpringListener(eventHandler);
    }
}
