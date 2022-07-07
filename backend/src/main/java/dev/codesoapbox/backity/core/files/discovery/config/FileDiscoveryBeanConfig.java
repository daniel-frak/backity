package dev.codesoapbox.backity.core.files.discovery.config;

import dev.codesoapbox.backity.core.files.discovery.adapters.driven.messaging.FileDiscoverySpringMessageService;
import dev.codesoapbox.backity.core.files.discovery.adapters.driven.persistence.DiscoveredFileJpaRepository;
import dev.codesoapbox.backity.core.files.discovery.adapters.driven.persistence.DiscoveredFileSpringRepository;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.discovery.domain.services.FileDiscoveryMessageService;
import dev.codesoapbox.backity.core.files.discovery.domain.services.FileDiscoveryService;
import dev.codesoapbox.backity.core.files.discovery.domain.services.SourceFileDiscoveryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.List;

@Configuration
public class FileDiscoveryBeanConfig {

    @Bean
    DiscoveredFileRepository discoveredFileRepository(DiscoveredFileSpringRepository springRepository) {
        return new DiscoveredFileJpaRepository(springRepository);
    }

    @Bean
    FileDiscoveryMessageService fileDiscoveryMessageService(SimpMessagingTemplate simpMessagingTemplate) {
        return new FileDiscoverySpringMessageService(simpMessagingTemplate);
    }

    @Bean
    FileDiscoveryService fileDiscoveryService(List<SourceFileDiscoveryService> discoveryServices,
                                              DiscoveredFileRepository repository,
                                              FileDiscoveryMessageService messageService) {
        return new FileDiscoveryService(discoveryServices, repository, messageService);
    }
}
