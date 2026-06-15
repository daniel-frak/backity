package dev.codesoapbox.backity.shared.infrastructure.config.events;

import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driving.eventlisteners.spring.DomainEventForwardingSpringListener;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringEventListenerSliceConfiguration;
import org.springframework.context.annotation.Bean;

@SpringEventListenerSliceConfiguration
public class DomainEventSpringListenerBeanConfig {

    @Bean
    DomainEventForwardingSpringListener domainEventForwardingSpringListener(DomainEventForwardingHandler eventHandler) {
        return new DomainEventForwardingSpringListener(eventHandler);
    }
}
