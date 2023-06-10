package dev.codesoapbox.backity.core.files.config;

import dev.codesoapbox.backity.core.files.adapters.driven.persistence.game.GameJpaRepository;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.game.JpaGameMapper;
import dev.codesoapbox.backity.core.files.adapters.driven.persistence.game.JpaGameSpringRepository;
import dev.codesoapbox.backity.core.files.application.GameFacade;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionRepository;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameBeanConfig {

    @Bean
    GameRepository gameRepository(JpaGameSpringRepository springRepository) {
        JpaGameMapper mapper = Mappers.getMapper(JpaGameMapper.class);
        return new GameJpaRepository(springRepository, mapper);
    }

    @Bean
    GameFacade gameFacade(GameRepository gameRepository, GameFileVersionRepository gameFileRepository) {
        return new GameFacade(gameRepository, gameFileRepository);
    }
}
