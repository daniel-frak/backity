package dev.codesoapbox.backity.core.game.infrastructure.config;

import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel.GameWithFileCopiesReadModelJpaRepository;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel.GameWithFileCopiesReadModelSpringRepository;
import dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa.readmodel.GameWithFilesCopiesReadModelJpaEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PageEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PaginationEntityMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameWithFileCopiesReadModelJpaRepositoryBeanConfig {

    @Bean
    GameWithFilesCopiesReadModelJpaEntityMapper entityMapper() {
        return Mappers.getMapper(GameWithFilesCopiesReadModelJpaEntityMapper.class);
    }

    @Bean
    GameWithFileCopiesReadModelJpaRepository gameWithFileCopiesReadModelJpaRepository(
            GameWithFileCopiesReadModelSpringRepository springRepository,
            GameWithFilesCopiesReadModelJpaEntityMapper entityMapper,
            PageEntityMapper pageEntityMapper,
            PaginationEntityMapper paginationEntityMapper
    ) {
        return new GameWithFileCopiesReadModelJpaRepository(springRepository, entityMapper,
                pageEntityMapper, paginationEntityMapper);
    }
}
