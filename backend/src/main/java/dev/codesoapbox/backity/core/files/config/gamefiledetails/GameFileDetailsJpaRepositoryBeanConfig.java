package dev.codesoapbox.backity.core.files.config.gamefiledetails;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaEntityMapper;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsJpaRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.GameFileDetailsSpringRepository;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
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
    GameFileDetailsRepository gameFileDetailsRepository(GameFileDetailsSpringRepository springRepository,
                                                        GameFileDetailsJpaEntityMapper entityMapper) {
        return new GameFileDetailsJpaRepository(springRepository, entityMapper);
    }
}
