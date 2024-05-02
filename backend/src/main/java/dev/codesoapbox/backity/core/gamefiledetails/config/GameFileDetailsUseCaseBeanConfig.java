package dev.codesoapbox.backity.core.gamefiledetails.config;

import dev.codesoapbox.backity.core.gamefiledetails.application.*;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameFileDetailsUseCaseBeanConfig {

    @Bean
    public EnqueueFileUseCase enqueueFileUseCase(GameFileDetailsRepository gameFileDetailsRepository) {
        return new EnqueueFileUseCase(gameFileDetailsRepository);
    }

    @Bean
    public GetCurrentlyDownloadingFileUseCase getCurrentlyDownloadingFileUseCase(
            GameFileDetailsRepository gameFileDetailsRepository) {
        return new GetCurrentlyDownloadingFileUseCase(gameFileDetailsRepository);
    }

    @Bean
    public GetDiscoveredFileListUseCase getDiscoveredFileListUseCase(
            GameFileDetailsRepository gameFileDetailsRepository) {
        return new GetDiscoveredFileListUseCase(gameFileDetailsRepository);
    }

    @Bean
    public GetEnqueuedFileListUseCase getEnqueuedFileListUseCase(
            GameFileDetailsRepository gameFileDetailsRepository) {
        return new GetEnqueuedFileListUseCase(gameFileDetailsRepository);
    }

    @Bean
    public GetProcessedFileListUseCase getProcessedFileListUseCase(
            GameFileDetailsRepository gameFileDetailsRepository) {
        return new GetProcessedFileListUseCase(gameFileDetailsRepository);
    }
}
