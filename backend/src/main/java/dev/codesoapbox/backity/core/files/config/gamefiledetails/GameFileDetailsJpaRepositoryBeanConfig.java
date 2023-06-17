package dev.codesoapbox.backity.core.files.config.gamefiledetails;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaEntityMapper;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaEntitySpringRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaRepository;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
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
