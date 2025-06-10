package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryProgressTracker;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import dev.codesoapbox.backity.core.discovery.application.GameProviderContentDiscoveryTracker;
import dev.codesoapbox.backity.core.discovery.application.GameProviderFileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.domain.GameContentDiscoveryResultRepository;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.util.List;
import java.util.concurrent.Executors;

@Configuration
public class GameContentDiscoveryBeanConfig {

    @Bean
    GameContentDiscoveryProgressTracker gameContentDiscoveryProgressTracker(
            Clock clock,
            DomainEventPublisher eventPublisher,
            GameContentDiscoveryResultRepository discoveryResultRepository,
            List<GameProviderFileDiscoveryService> discoveryServices) {
        return new GameContentDiscoveryProgressTracker(
                clock, eventPublisher, discoveryResultRepository, discoveryServices,
                GameProviderContentDiscoveryTracker::new);
    }

    @Bean
    GameContentDiscoveryService gameContentDiscoveryService(
            List<GameProviderFileDiscoveryService> discoveryServices,
            GameRepository gameRepository,
            GameFileRepository fileRepository,
            GameContentDiscoveryProgressTracker discoveryProgressTracker) {
        return new GameContentDiscoveryService(discoveryServices, gameRepository, fileRepository,
                discoveryProgressTracker, Executors.newVirtualThreadPerTaskExecutor());
    }
}
