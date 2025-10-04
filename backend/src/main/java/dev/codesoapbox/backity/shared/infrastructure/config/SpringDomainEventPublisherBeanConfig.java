package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.spring.SpringDomainEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringApplicationEventPublisherBeanConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;

@SpringApplicationEventPublisherBeanConfiguration
public class SpringDomainEventPublisherBeanConfig {

    @Bean
    SpringDomainEventPublisher domainEventSpringPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new SpringDomainEventPublisher(applicationEventPublisher);
    }
}
