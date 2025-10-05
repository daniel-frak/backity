package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageableMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxJpaRepository;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxJpaSerializer;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox.DomainEventOutboxSpringRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.JpaRepositoryBeanConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@JpaRepositoryBeanConfiguration
public class OutboxEventJpaRepositoryBeanConfig {

    @Bean
    DomainEventOutboxJpaRepository domainEventJpaOutboxRepository(Clock clock,
                                                                  DomainEventOutboxSpringRepository springRepository,
                                                                  SpringPageMapper pageMapper,
                                                                  SpringPageableMapper paginationMapper,
                                                                  List<DomainEventOutboxJpaSerializer<?>> serializers) {
        Map<Class<? extends DomainEvent>, DomainEventOutboxJpaSerializer<?>> eventSerializers = serializers.stream()
                .collect(Collectors.toMap(DomainEventOutboxJpaSerializer::getSupportedEventClass, Function.identity()));
        return new DomainEventOutboxJpaRepository(
                clock, springRepository, pageMapper, paginationMapper, eventSerializers);
    }
}
