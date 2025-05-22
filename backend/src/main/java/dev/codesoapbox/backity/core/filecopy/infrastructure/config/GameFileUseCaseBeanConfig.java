package dev.codesoapbox.backity.core.filecopy.infrastructure.config;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.application.usecases.GetCurrentlyDownloadingFileCopyUseCase;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameFileUseCaseBeanConfig {

    @Bean
    public GetCurrentlyDownloadingFileCopyUseCase getCurrentlyDownloadingFileCopyUseCase(
            FileCopyRepository fileCopyRepository, GameFileRepository gameFileRepository,
            GameRepository gameRepository) {
        return new GetCurrentlyDownloadingFileCopyUseCase(fileCopyRepository, gameFileRepository, gameRepository);
    }
}
