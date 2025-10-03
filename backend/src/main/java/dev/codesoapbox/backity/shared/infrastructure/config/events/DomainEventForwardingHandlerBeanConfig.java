package dev.codesoapbox.backity.shared.infrastructure.config.events;

import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.DomainEventHandlerBeanConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Map;

@DomainEventHandlerBeanConfiguration
public class DomainEventForwardingHandlerBeanConfig {

    @Bean
    DomainEventForwardingHandler domainEventForwardingHandler(ApplicationContext applicationContext) {
        var eventForwarderFinder = new DomainEventForwarderFinder(applicationContext);

        @SuppressWarnings("java:S6411") // Cannot implement Comparable for Class type
        Map<Class<?>, List<DomainEventForwardingHandler.EventForwardingConsumer<?>>>
                eventForwardingConsumersByDomainEventClass =
                eventForwarderFinder.findEventForwardingConsumersByEventClass();

        return new DomainEventForwardingHandler(eventForwardingConsumersByDomainEventClass);
    }
}
