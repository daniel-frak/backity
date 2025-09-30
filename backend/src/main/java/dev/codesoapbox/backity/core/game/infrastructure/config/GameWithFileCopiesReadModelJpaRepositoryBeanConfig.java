package dev.codesoapbox.backity.core.game.infrastructure.config;

import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel.GameWithFileCopiesReadModelJpaRepository;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel.GameWithFileCopiesReadModelSpringRepository;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel.GameWithFilesCopiesReadModelJpaEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageableMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.JpaRepositoryBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@JpaRepositoryBeanConfiguration
public class GameWithFileCopiesReadModelJpaRepositoryBeanConfig {

    @Bean
    GameWithFilesCopiesReadModelJpaEntityMapper entityMapper() {
        return Mappers.getMapper(GameWithFilesCopiesReadModelJpaEntityMapper.class);
    }

    @Bean
    GameWithFileCopiesReadModelJpaRepository gameWithFileCopiesReadModelJpaRepository(
            GameWithFileCopiesReadModelSpringRepository springRepository,
            GameWithFilesCopiesReadModelJpaEntityMapper entityMapper,
            SpringPageMapper springPageMapper,
            SpringPageableMapper springPageableMapper
    ) {
        return new GameWithFileCopiesReadModelJpaRepository(springRepository, entityMapper,
                springPageMapper, springPageableMapper);
    }
}
