package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.InternalApplicationServiceBeanConfiguration;
import org.springframework.context.annotation.Bean;

@InternalApplicationServiceBeanConfiguration
public class SharedFileManagementInternalServiceBeanConfig {

    @Bean
    UniqueFilePathResolver uniqueFilePathResolver() {
        return new UniqueFilePathResolver();
    }
}
