package dev.codesoapbox.backity.core.gamefiledetails.config;

import dev.codesoapbox.backity.core.gamefiledetails.adapters.driven.persistence.jpa.GameFileDetailsJpaEntityMapper;
import dev.codesoapbox.backity.core.gamefiledetails.adapters.driven.persistence.jpa.GameFileDetailsJpaEntitySpringRepository;
import dev.codesoapbox.backity.core.gamefiledetails.adapters.driven.persistence.jpa.GameFileDetailsJpaRepository;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PageEntityMapper;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PaginationEntityMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameFileDetailsJpaRepositoryBeanConfig {

    @Bean
    GameFileDetailsJpaEntityMapper gameFileDetailsJpaEntityMapper() {
        return Mappers.getMapper(GameFileDetailsJpaEntityMapper.class);
    }

    @Bean
    GameFileDetailsRepository gameFileDetailsRepository(GameFileDetailsJpaEntitySpringRepository springRepository,
                                                        GameFileDetailsJpaEntityMapper entityMapper,
                                                        PageEntityMapper pageMapper,
                                                        PaginationEntityMapper paginationMapper) {
        return new GameFileDetailsJpaRepository(springRepository, entityMapper, pageMapper, paginationMapper);
    }
}
