package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolutionRepository;
import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.persistence.hardcoded.HardCodedStorageSolutionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class StorageSolutionHardCodedRepositoryBeanConfig {

    @Bean
    StorageSolutionRepository storageSolutionRepository(List<StorageSolution> storageSolutions) {
        return new HardCodedStorageSolutionRepository(storageSolutions);
    }
}
