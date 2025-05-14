package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.infrastructure.adapters.driven.filesystem.LocalFileSystemStorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(value = "backity.filesystem.s3.enabled", havingValue = "false")
@Configuration
public class LocalFileSystemBeanConfig {

    @Bean
    StorageSolution localFileSystemStorageSolution() {
        return new LocalFileSystemStorageSolution();
    }
}
