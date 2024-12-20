package dev.codesoapbox.backity.core.discovery.config;

import dev.codesoapbox.backity.core.discovery.application.GetFileDiscoveryStatusListUseCase;
import dev.codesoapbox.backity.core.discovery.application.StartFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.application.StopFileDiscoveryUseCase;
import dev.codesoapbox.backity.core.discovery.application.FileDiscoveryService;
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
