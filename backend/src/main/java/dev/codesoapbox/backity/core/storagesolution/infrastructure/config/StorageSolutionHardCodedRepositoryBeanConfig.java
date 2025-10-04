package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.persistence.hardcoded.HardCodedStorageSolutionRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.InMemoryRepositoryBeanConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.List;

@InMemoryRepositoryBeanConfiguration
public class StorageSolutionHardCodedRepositoryBeanConfig {

    @Bean
    StorageSolutionRepository storageSolutionRepository(List<StorageSolution> storageSolutions) {
        return new HardCodedStorageSolutionRepository(storageSolutions);
    }
}
