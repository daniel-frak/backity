package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import dev.codesoapbox.backity.core.discovery.application.GameProviderFileDiscoveryService;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GameContentDiscoveryBeanConfig {

    @Bean
    GameContentDiscoveryService gameContentDiscoveryService(List<GameProviderFileDiscoveryService> discoveryServices,
                                                     GameRepository gameRepository,
                                                     GameFileRepository fileRepository,
                                                     DomainEventPublisher eventPublisher) {
        return new GameContentDiscoveryService(discoveryServices, gameRepository, fileRepository, eventPublisher);
    }
}
