package dev.codesoapbox.backity.core.game.config;

import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.game.application.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameUseCaseBeanConfig {

    @Bean
    GetGamesWithFilesUseCase getGamesWithFilesUseCase(
            GameRepository gameRepository, GameFileRepository gameFileRepository) {
        return new GetGamesWithFilesUseCase(gameRepository, gameFileRepository);
    }
}
