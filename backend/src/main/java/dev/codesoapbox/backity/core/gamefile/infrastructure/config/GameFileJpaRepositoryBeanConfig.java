package dev.codesoapbox.backity.core.gamefile.infrastructure.config;

import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa.GameFileJpaEntityMapper;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa.GameFileJpaEntitySpringRepository;
import dev.codesoapbox.backity.core.gamefile.infrastructure.adapters.driven.persistence.jpa.GameFileJpaRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.JpaRepositoryBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@JpaRepositoryBeanConfiguration
public class GameFileJpaRepositoryBeanConfig {

    @Bean
    GameFileJpaEntityMapper gameFileJpaEntityMapper() {
        return Mappers.getMapper(GameFileJpaEntityMapper.class);
    }

    @Bean
    GameFileRepository gameFileRepository(GameFileJpaEntitySpringRepository springRepository,
                                          GameFileJpaEntityMapper entityMapper) {
        return new GameFileJpaRepository(springRepository, entityMapper);
    }
}
