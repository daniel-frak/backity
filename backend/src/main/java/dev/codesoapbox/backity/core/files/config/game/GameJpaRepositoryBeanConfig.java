package dev.codesoapbox.backity.core.files.config.game;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.game.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.game.GameJpaEntitySpringRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.game.GameJpaRepository;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PageEntityMapper;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PaginationEntityMapper;
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
                                     GameJpaEntityMapper entityMapper, PageEntityMapper pageEntityMapper,
                                     PaginationEntityMapper paginationEntityMapper) {
        return new GameJpaRepository(springRepository, entityMapper, pageEntityMapper, paginationEntityMapper);
    }
}
