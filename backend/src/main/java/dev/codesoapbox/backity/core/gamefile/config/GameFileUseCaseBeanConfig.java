package dev.codesoapbox.backity.core.gamefile.config;

import dev.codesoapbox.backity.core.gamefile.application.*;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameFileUseCaseBeanConfig {

    @Bean
    public EnqueueFileUseCase enqueueFileUseCase(GameFileRepository gameFileRepository) {
        return new EnqueueFileUseCase(gameFileRepository);
    }

    @Bean
    public GetCurrentlyDownloadingFileUseCase getCurrentlyDownloadingFileUseCase(
            GameFileRepository gameFileRepository) {
        return new GetCurrentlyDownloadingFileUseCase(gameFileRepository);
    }

    @Bean
    public GetDiscoveredFileListUseCase getDiscoveredFileListUseCase(
            GameFileRepository gameFileRepository) {
        return new GetDiscoveredFileListUseCase(gameFileRepository);
    }

    @Bean
    public GetEnqueuedFileListUseCase getEnqueuedFileListUseCase(
            GameFileRepository gameFileRepository) {
        return new GetEnqueuedFileListUseCase(gameFileRepository);
    }

    @Bean
    public GetProcessedFileListUseCase getProcessedFileListUseCase(
            GameFileRepository gameFileRepository) {
        return new GetProcessedFileListUseCase(gameFileRepository);
    }
}
