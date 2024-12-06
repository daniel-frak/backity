package dev.codesoapbox.backity.core.discovery.config;

import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.domain.GameProviderFileDiscoveryService;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.shared.domain.DomainEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FileDiscoveryBeanConfig {

    @Bean
    FileDiscoveryService fileDiscoveryService(List<GameProviderFileDiscoveryService> discoveryServices,
                                              GameRepository gameRepository,
                                              GameFileRepository fileRepository,
                                              DomainEventPublisher eventPublisher) {
        return new FileDiscoveryService(discoveryServices, gameRepository, fileRepository, eventPublisher);
    }
}
