package dev.codesoapbox.backity.core.discovery.config;

import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.FileDiscoverySpringMessageService;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveredMessageMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryProgressUpdateMessageMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.model.FileDiscoveryStatusChangedMessageMapper;
import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryMessageService;
import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryService;
import dev.codesoapbox.backity.core.discovery.domain.SourceFileDiscoveryService;
import dev.codesoapbox.backity.core.game.domain.GameRepository;
import dev.codesoapbox.backity.core.gamefiledetails.domain.GameFileDetailsRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Configuration
public class FileDiscoveryBeanConfig {

    @Bean
    FileDiscoveryMessageService fileDiscoveryMessageService(SimpMessagingTemplate simpMessagingTemplate) {
        FileDiscoveredMessageMapper fileDiscoveredMessageMapper = Mappers.getMapper(FileDiscoveredMessageMapper.class);
        FileDiscoveryStatusChangedMessageMapper fileDiscoveryStatusChangedMessageMapper =
                Mappers.getMapper(FileDiscoveryStatusChangedMessageMapper.class);
        FileDiscoveryProgressUpdateMessageMapper fileDiscoveryProgressUpdateMessageMapper =
                Mappers.getMapper(FileDiscoveryProgressUpdateMessageMapper.class);
        return new FileDiscoverySpringMessageService(simpMessagingTemplate, fileDiscoveredMessageMapper,
                fileDiscoveryStatusChangedMessageMapper, fileDiscoveryProgressUpdateMessageMapper);
    }

    @Bean
    FileDiscoveryService fileDiscoveryService(List<SourceFileDiscoveryService> discoveryServices,
                                              GameRepository gameRepository,
                                              GameFileDetailsRepository fileRepository,
                                              FileDiscoveryMessageService messageService) {
        return new FileDiscoveryService(discoveryServices, gameRepository, fileRepository, messageService);
    }
}
