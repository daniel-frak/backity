package dev.codesoapbox.backity.core.game.infrastructure.config;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesReadModelRepository;
import dev.codesoapbox.backity.core.game.application.usecases.GetGamesWithFilesUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameUseCaseBeanConfig {

    @Bean
    GetGamesWithFilesUseCase getGamesWithFilesUseCase(
            GameWithFileCopiesReadModelRepository gameReadModelRepository,
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new GetGamesWithFilesUseCase(gameReadModelRepository, replicationProgressRepository);
    }
}
