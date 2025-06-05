package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.application.GetStorageSolutionStatusesUseCase;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageSolutionUseCaseBeanConfig {

    @Bean
    GetStorageSolutionStatusesUseCase getStorageSolutionStatusesUseCase(
            StorageSolutionRepository storageSolutionRepository) {
        return new GetStorageSolutionStatusesUseCase(storageSolutionRepository);
    }
}
