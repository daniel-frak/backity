package dev.codesoapbox.backity.core.game.config;

import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import dev.codesoapbox.backity.core.game.application.GameFacade;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameBeanConfig {

    @Bean
    GameFacade gameFacade(GameRepository gameRepository, FileDetailsRepository fileDetailsRepository) {
        return new GameFacade(gameRepository, fileDetailsRepository);
    }
}
