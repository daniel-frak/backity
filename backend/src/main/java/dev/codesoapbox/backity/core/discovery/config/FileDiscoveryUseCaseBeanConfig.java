package dev.codesoapbox.backity.core.discovery.config;

import dev.codesoapbox.backity.core.discovery.adapters.application.GetFileDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.adapters.application.StartFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.adapters.application.StopFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.domain.FileDiscoveryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileDiscoveryUseCaseBeanConfig {

    @Bean
    public GetFileDiscoveryStatusListUseCase getFileDiscoveryStatusListUseCase(
            FileDiscoveryService fileDiscoveryService) {
        return new GetFileDiscoveryStatusListUseCase(fileDiscoveryService);
    }

    @Bean
    public StartFileDiscoveryUseCase startFileDiscoveryUseCase(FileDiscoveryService fileDiscoveryService) {
        return new StartFileDiscoveryUseCase(fileDiscoveryService);
    }

    @Bean
    public StopFileDiscoveryUseCase stopFileDiscoveryUseCase(FileDiscoveryService fileDiscoveryService) {
        return new StopFileDiscoveryUseCase(fileDiscoveryService);
    }
}
