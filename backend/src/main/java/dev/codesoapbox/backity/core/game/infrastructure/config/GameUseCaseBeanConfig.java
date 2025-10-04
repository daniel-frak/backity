package dev.codesoapbox.backity.core.game.infrastructure.config;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.game.application.GameWithFileCopiesReadModelRepository;
import dev.codesoapbox.backity.core.game.application.usecases.GetGamesWithFilesUseCase;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseBeanConfiguration;
import org.springframework.context.annotation.Bean;

@UseCaseBeanConfiguration
public class GameUseCaseBeanConfig {

    @Bean
    GetGamesWithFilesUseCase getGamesWithFilesUseCase(
            GameWithFileCopiesReadModelRepository gameReadModelRepository,
            FileCopyReplicationProgressRepository replicationProgressRepository) {
        return new GetGamesWithFilesUseCase(gameReadModelRepository, replicationProgressRepository);
    }
}
