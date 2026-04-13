package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.spring.SpringDomainEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox.AnnotationBasedJsonEventSerializer;
import dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox.AnnotationBasedOutboxEventMapperRegistry;
import dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox.OutboxEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringApplicationEventPublisherBeanConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.modulith.events.core.EventSerializer;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@SpringApplicationEventPublisherBeanConfiguration
public class SpringDomainEventPublisherBeanConfig {

    @Bean
    SpringDomainEventPublisher domainEventSpringPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new SpringDomainEventPublisher(applicationEventPublisher);
    }

    @Primary
    @Bean
    EventSerializer eventSerializer(JsonMapper jsonMapper, ApplicationContext context) {
        List<Object> eventMappers = context.getBeansWithAnnotation(OutboxEventMapper.class).values()
                .stream()
                .toList();

        var outboxMapperRegistry = new AnnotationBasedOutboxEventMapperRegistry(eventMappers);

        return new AnnotationBasedJsonEventSerializer(jsonMapper, outboxMapperRegistry);
    }
}
