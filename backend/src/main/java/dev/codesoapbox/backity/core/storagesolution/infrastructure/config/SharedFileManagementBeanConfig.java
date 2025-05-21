package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.UniqueFilePathResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedFileManagementBeanConfig {

    @Bean
    UniqueFilePathResolver filePathProvider(@Value("${backity.default-path-template}") String defaultPathTemplate,
                                            StorageSolution storageSolution) {
        return new UniqueFilePathResolver(defaultPathTemplate, storageSolution);
    }
}
