package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryProgressTracker;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import dev.codesoapbox.backity.core.discovery.application.usecases.GetGameContentDiscoveryOverviewsUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StartGameContentDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.application.usecases.StopGameContentDiscoveryUseCase;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseSliceConfiguration;
import org.springframework.context.annotation.Bean;

@UseCaseSliceConfiguration
public class GameContentDiscoveryUseCaseBeanConfig {

    @Bean
    GetGameContentDiscoveryOverviewsUseCase getGameContentDiscoveryOverviewsUseCase(
            GameContentDiscoveryProgressTracker discoveryProgressTracker) {
        return new GetGameContentDiscoveryOverviewsUseCase(discoveryProgressTracker);
    }

    @Bean
    StartGameContentDiscoveryUseCase startGameContentDiscoveryUseCase(
            GameContentDiscoveryService gameContentDiscoveryService) {
        return new StartGameContentDiscoveryUseCase(gameContentDiscoveryService);
    }

    @Bean
    StopGameContentDiscoveryUseCase stopGameContentDiscoveryUseCase(
            GameContentDiscoveryService gameContentDiscoveryService) {
        return new StopGameContentDiscoveryUseCase(gameContentDiscoveryService);
    }
}
