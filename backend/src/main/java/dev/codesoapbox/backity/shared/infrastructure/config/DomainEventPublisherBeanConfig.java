package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.spring.DomainEventSpringPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainEventPublisherBeanConfig {

    @Bean
    DomainEventSpringPublisher domainEventSpringPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new DomainEventSpringPublisher(applicationEventPublisher);
    }
}
