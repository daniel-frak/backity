package dev.codesoapbox.backity.core.files.config.game;

import dev.codesoapbox.backity.core.files.application.GameFacade;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileDetailsRepository;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameBeanConfig {

    @Bean
    GameFacade gameFacade(GameRepository gameRepository, GameFileDetailsRepository gameFileRepository) {
        return new GameFacade(gameRepository, gameFileRepository);
    }
}
