package dev.codesoapbox.backity.shared.infrastructure.config.events;

import dev.codesoapbox.backity.shared.application.events.outbox.DomainEventOutboxProcessor;
import dev.codesoapbox.backity.shared.application.events.outbox.DomainEventOutboxRepository;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.InternalApplicationServiceBeanConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

// @TODO is this the correct annotation?
@InternalApplicationServiceBeanConfiguration
public class DomainEventOutboxProcessorBeanConfig {

    @Bean
    DomainEventOutboxProcessor domainEventOutboxProcessor(DomainEventOutboxRepository outboxRepository,
                                                          ApplicationEventPublisher eventPublisher) {
        return new DomainEventOutboxProcessor(outboxRepository, eventPublisher);
    }

    // @TODO Move elsewhere?
    @Bean
    DomainEventOutboxPublisher domainEventOutboxPublisher(DomainEventOutboxRepository outboxRepository) {
        return new DomainEventOutboxPublisher(outboxRepository);
    }
}
