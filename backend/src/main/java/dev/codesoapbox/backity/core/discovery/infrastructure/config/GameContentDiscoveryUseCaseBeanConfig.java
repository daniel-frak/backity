package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryProgressTracker;
import dev.codesoapbox.backity.core.discovery.application.usecases.GetGameContentDiscoveryOverviewsUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StartGameContentDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StopGameContentDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameContentDiscoveryUseCaseBeanConfig {

    @Bean
    public GetGameContentDiscoveryOverviewsUseCase getGameContentDiscoveryOverviewsUseCase(
            GameContentDiscoveryProgressTracker discoveryProgressTracker) {
        return new GetGameContentDiscoveryOverviewsUseCase(discoveryProgressTracker);
    }

    @Bean
    public StartGameContentDiscoveryUseCase startGameContentDiscoveryUseCase(
            GameContentDiscoveryService gameContentDiscoveryService) {
        return new StartGameContentDiscoveryUseCase(gameContentDiscoveryService);
    }

    @Bean
    public StopGameContentDiscoveryUseCase stopGameContentDiscoveryUseCase(
            GameContentDiscoveryService gameContentDiscoveryService) {
        return new StopGameContentDiscoveryUseCase(gameContentDiscoveryService);
    }
}
