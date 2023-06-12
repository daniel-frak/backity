package dev.codesoapbox.backity.core.files.config;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.game.GameJpaEntityMapper;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.game.GameJpaEntitySpringRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.game.GameJpaRepository;
import dev.codesoapbox.backity.core.files.application.GameFacade;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameBeanConfig {

    @Bean
    GameRepository gameRepository(GameJpaEntitySpringRepository springRepository) {
        GameJpaEntityMapper mapper = Mappers.getMapper(GameJpaEntityMapper.class);
        return new GameJpaRepository(springRepository, mapper);
    }

    @Bean
    GameFacade gameFacade(GameRepository gameRepository, GameFileDetailsRepository gameFileRepository) {
        return new GameFacade(gameRepository, gameFileRepository);
    }
}
