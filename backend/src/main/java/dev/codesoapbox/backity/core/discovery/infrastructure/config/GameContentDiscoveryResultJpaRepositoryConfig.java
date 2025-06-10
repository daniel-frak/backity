package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.GameContentDiscoveryResultJpaEntityMapper;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.GameContentDiscoveryResultJpaRepository;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.GameContentDiscoveryResultSpringRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameContentDiscoveryResultJpaRepositoryConfig {

    @Bean
    GameContentDiscoveryResultJpaEntityMapper gameContentDiscoveryResultJpaEntityMapper() {
        return Mappers.getMapper(GameContentDiscoveryResultJpaEntityMapper.class);
    }

    @Bean
    GameContentDiscoveryResultJpaRepository gameContentDiscoveryResultJpaRepository(
            GameContentDiscoveryResultSpringRepository springRepository,
            GameContentDiscoveryResultJpaEntityMapper mapper) {
        return new GameContentDiscoveryResultJpaRepository(springRepository, mapper);
    }
}
