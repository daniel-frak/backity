package dev.codesoapbox.backity.core.storagesolution.infrastructure.config;

import dev.codesoapbox.backity.core.storagesolution.domain.StorageSolution;
import dev.codesoapbox.backity.core.storagesolution.domain.FilePathProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedFileManagementBeanConfig {

    @Bean
    FilePathProvider filePathProvider(@Value("${backity.default-path-template}") String defaultPathTemplate,
                                      StorageSolution storageSolution) {
        return new FilePathProvider(defaultPathTemplate, storageSolution);
    }
}
