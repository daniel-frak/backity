package dev.codesoapbox.backity.core.files.config;

import dev.codesoapbox.backity.core.files.adapters.driven.messaging.FileDiscoverySpringMessageService;
import dev.codesoapbox.backity.core.files.domain.backup.repositories.GameFileVersionBackupRepository;
import dev.codesoapbox.backity.core.files.domain.discovery.services.FileDiscoveryMessageService;
import dev.codesoapbox.backity.core.files.domain.discovery.services.FileDiscoveryService;
import dev.codesoapbox.backity.core.files.domain.discovery.services.SourceFileDiscoveryService;
import dev.codesoapbox.backity.core.files.domain.game.GameRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Configuration
public class FileDiscoveryBeanConfig {

    @Bean
    FileDiscoveryMessageService fileDiscoveryMessageService(SimpMessagingTemplate simpMessagingTemplate) {
        return new FileDiscoverySpringMessageService(simpMessagingTemplate);
    }

    @Bean
    FileDiscoveryService fileDiscoveryService(List<SourceFileDiscoveryService> discoveryServices,
                                              GameRepository gameRepository,
                                              GameFileVersionBackupRepository fileRepository,
                                              FileDiscoveryMessageService messageService) {
        return new FileDiscoveryService(discoveryServices, gameRepository, fileRepository, messageService);
    }
}
