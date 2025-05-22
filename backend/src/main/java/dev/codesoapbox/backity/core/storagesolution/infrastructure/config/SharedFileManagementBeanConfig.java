package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedFileManagementBeanConfig {

    @Bean
    UniqueFilePathResolver uniqueFilePathResolver() {
        return new UniqueFilePathResolver();
    }
}
