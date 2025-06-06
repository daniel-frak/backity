package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryProgressRepository;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.inmemory.InMemoryGameContentDiscoveryProgressRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameContentDiscoveryRepositoryBeanConfig {

    @Bean
    GameContentDiscoveryProgressRepository gameContentDiscoveryProgressRepository() {
        return new InMemoryGameContentDiscoveryProgressRepository();
    }
}
