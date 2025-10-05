package dev.codesoapbox.backity.shared.infrastructure.config.events;

import dev.codesoapbox.backity.shared.application.events.outbox.DomainEventOutboxProcessor;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.OutboxProcessingSpringScheduler;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringSchedulerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@SpringSchedulerBeanConfiguration
public class DomainEventOutboxSpringSchedulerBeanConfig {

    @Bean
    OutboxProcessingSpringScheduler outboxProcessingSpringScheduler(DomainEventOutboxProcessor outboxProcessor) {
        return new OutboxProcessingSpringScheduler(outboxProcessor);
    }
}
