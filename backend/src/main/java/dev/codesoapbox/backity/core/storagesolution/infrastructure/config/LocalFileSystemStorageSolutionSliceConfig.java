package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem.LocalFileSystemStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.infrastructure.config.slices.LocalFileSystemStorageSolutionSliceConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

@ConditionalOnProperty(value = "backity.filesystem.local.enabled", havingValue = "true")
@LocalFileSystemStorageSolutionSliceConfiguration
public class LocalFileSystemStorageSolutionSliceConfig {

    @Bean
    StorageSolution localFileSystemStorageSolution() {
        return new LocalFileSystemStorageSolution();
    }
}
