package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.application.GetStorageSolutionStatusesUseCase;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseBeanConfiguration;
import org.springframework.context.annotation.Bean;

@UseCaseBeanConfiguration
public class StorageSolutionUseCaseBeanConfig {

    @Bean
    GetStorageSolutionStatusesUseCase getStorageSolutionStatusesUseCase(
            StorageSolutionRepository storageSolutionRepository) {
        return new GetStorageSolutionStatusesUseCase(storageSolutionRepository);
    }
}
