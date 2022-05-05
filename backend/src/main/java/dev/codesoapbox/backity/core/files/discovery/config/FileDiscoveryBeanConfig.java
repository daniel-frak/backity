package dev.codesoapbox.backity.core.files.discovery.config;

import dev.codesoapbox.backity.core.files.discovery.adapters.driven.persistence.DiscoveredFileJpaRepository;
import dev.codesoapbox.backity.core.files.discovery.adapters.driven.persistence.DiscoveredFileSpringRepository;
import dev.codesoapbox.backity.core.files.discovery.domain.repositories.DiscoveredFileRepository;
import dev.codesoapbox.backity.core.files.discovery.domain.services.FileDiscoveryService;
import dev.codesoapbox.backity.core.files.discovery.domain.services.SourceFileDiscoveryService;
import dev.codesoapbox.backity.core.shared.domain.services.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FileDiscoveryBeanConfig {

    @Bean
    DiscoveredFileRepository discoveredFileRepository(DiscoveredFileSpringRepository springRepository) {
        return new DiscoveredFileJpaRepository(springRepository);
    }

    @Bean
    FileDiscoveryService fileDiscoveryService(List<SourceFileDiscoveryService> discoveryServices,
                                              DiscoveredFileRepository repository,
                                              MessageService messageService) {
        return new FileDiscoveryService(discoveryServices, repository, messageService);
    }
}
