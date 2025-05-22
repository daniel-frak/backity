package dev.codesoapbox.backity.core.discovery.infrastructure.config;

import dev.codesoapbox.backity.core.discovery.application.FileCopyFactory;
import dev.codesoapbox.backity.core.discovery.application.GameContentDiscoveryService;
import dev.codesoapbox.backity.core.discovery.application.GameProviderFileDiscoveryService;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.FileCopyId;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GameContentDiscoveryBeanConfig {

    @Bean
    FileCopyFactory fileCopyFactory() {
        return new FileCopyFactory(FileCopyId::newInstance);
    }

    @Bean
    GameContentDiscoveryService gameContentDiscoveryService(List<GameProviderFileDiscoveryService> discoveryServices,
                                                     FileCopyFactory fileCopyFactory,
                                                     GameRepository gameRepository,
                                                     GameFileRepository fileRepository,
                                                     DomainEventPublisher eventPublisher) {
        return new GameContentDiscoveryService(
                discoveryServices, fileCopyFactory, gameRepository, fileRepository, eventPublisher);
    }
}
