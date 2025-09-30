package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.GameContentDiscoveryResultJpaEntityMapper;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.GameContentDiscoveryResultJpaRepository;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.persistence.jpa.GameContentDiscoveryResultSpringRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.JpaRepositoryBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@JpaRepositoryBeanConfiguration
public class GameContentDiscoveryResultJpaRepositoryBeanConfig {

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
