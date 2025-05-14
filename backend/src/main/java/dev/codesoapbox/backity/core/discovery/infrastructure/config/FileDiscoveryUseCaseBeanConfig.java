package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.application.usecases.GetFileDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StartFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StopFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileDiscoveryUseCaseBeanConfig {

    @Bean
    public GetFileDiscoveryStatusListUseCase getFileDiscoveryStatusListUseCase(
            GameContentDiscoveryService gameContentDiscoveryService) {
        return new GetFileDiscoveryStatusListUseCase(gameContentDiscoveryService);
    }

    @Bean
    public StartFileDiscoveryUseCase startFileDiscoveryUseCase(GameContentDiscoveryService gameContentDiscoveryService) {
        return new StartFileDiscoveryUseCase(gameContentDiscoveryService);
    }

    @Bean
    public StopFileDiscoveryUseCase stopFileDiscoveryUseCase(GameContentDiscoveryService gameContentDiscoveryService) {
        return new StopFileDiscoveryUseCase(gameContentDiscoveryService);
    }
}
