package dev.codesoapbox.backity.core.game.infrastructure.config;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.game.application.usecases.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameUseCaseBeanConfig {

    @Bean
    GetGamesWithFilesUseCase getGamesWithFilesUseCase(
            GameRepository gameRepository, GameFileRepository gameFileRepository,
            FileCopyRepository fileCopyRepository) {
        return new GetGamesWithFilesUseCase(gameRepository, gameFileRepository,
                fileCopyRepository);
    }
}
