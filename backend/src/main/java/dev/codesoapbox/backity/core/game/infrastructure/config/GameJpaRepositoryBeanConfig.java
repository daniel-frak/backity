package dev.codesoapbox.backity.core.game.infrastructure.config;

import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaEntitySpringRepository;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.GameJpaRepository;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageableMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameJpaRepositoryBeanConfig {

    @Bean
    GameJpaEntityMapper gameJpaEntityMapper() {
        return Mappers.getMapper(GameJpaEntityMapper.class);
    }

    @Bean
    GameJpaRepository gameRepository(GameJpaEntitySpringRepository springRepository,
                                     GameJpaEntityMapper entityMapper, SpringPageMapper springPageMapper,
                                     SpringPageableMapper springPageableMapper) {
        return new GameJpaRepository(springRepository, entityMapper, springPageMapper, springPageableMapper);
    }
}
