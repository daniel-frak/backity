package dev.codesoapbox.backity.core.gamefile.infrastructure.config;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.gamefile.application.usecases.GetCurrentlyDownloadingFileUseCase;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameFileUseCaseBeanConfig {

    // @TODO Consider changing this use case to retrieve FileCopy
    @Bean
    public GetCurrentlyDownloadingFileUseCase getCurrentlyDownloadingFileUseCase(
            FileCopyRepository fileCopyRepository,
            GameFileRepository gameFileRepository) {
        return new GetCurrentlyDownloadingFileUseCase(fileCopyRepository, gameFileRepository);
    }
}
