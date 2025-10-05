package dev.codesoapbox.backity.core.discovery.infrastructure.config.events;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.outbox.GameContentDiscoveryProgressChangedEventOutboxJpaSerializer;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.outbox.GameContentDiscoveryStartedEventOutboxJpaSerializer;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.outbox.GameContentDiscoveryStoppedEventOutboxJpaSerializer;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.OutboxJpaSerializerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@OutboxJpaSerializerBeanConfiguration
public class GameContentDiscoveryOutboxJpaSerializerBeanConfig {

    @Bean
    GameContentDiscoveryProgressChangedEventOutboxJpaSerializer
    gameContentDiscoveryProgressChangedEventOutboxJpaSerializer() {
        return new GameContentDiscoveryProgressChangedEventOutboxJpaSerializer();
    }

    @Bean
    GameContentDiscoveryStartedEventOutboxJpaSerializer gameContentDiscoveryStartedEventOutboxJpaSerializer() {
        return new GameContentDiscoveryStartedEventOutboxJpaSerializer();
    }

    @Bean
    GameContentDiscoveryStoppedEventOutboxJpaSerializer gameContentDiscoveryStoppedEventOutboxJpaSerializer() {
        return new GameContentDiscoveryStoppedEventOutboxJpaSerializer();
    }
}
