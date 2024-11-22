package dev.codesoapbox.backity.core.discovery.config;

import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.FileDiscoveryEventWebSocketPublisher;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveredWsEventMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryProgressChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryStatusChangedWsEventMapper;
import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryEventPublisher;
import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.domain.GameProviderFileDiscoveryService;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Configuration
public class FileDiscoveryBeanConfig {

    @Bean
    FileDiscoveryEventPublisher fileDiscoveryEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        FileDiscoveredWsEventMapper fileDiscoveredWsEventMapper = Mappers.getMapper(FileDiscoveredWsEventMapper.class);
        FileDiscoveryStatusChangedWsEventMapper fileDiscoveryStatusChangedWsEventMapper =
                Mappers.getMapper(FileDiscoveryStatusChangedWsEventMapper.class);
        FileDiscoveryProgressChangedWsEventMapper fileDiscoveryProgressChangedWsEventMapper =
                Mappers.getMapper(FileDiscoveryProgressChangedWsEventMapper.class);
        return new FileDiscoveryEventWebSocketPublisher(simpMessagingTemplate, fileDiscoveredWsEventMapper,
                fileDiscoveryStatusChangedWsEventMapper, fileDiscoveryProgressChangedWsEventMapper);
    }

    @Bean
    FileDiscoveryService fileDiscoveryService(List<GameProviderFileDiscoveryService> discoveryServices,
                                              GameRepository gameRepository,
                                              GameFileRepository fileRepository,
                                              FileDiscoveryEventPublisher eventPublisher) {
        return new FileDiscoveryService(discoveryServices, gameRepository, fileRepository, eventPublisher);
    }
}
