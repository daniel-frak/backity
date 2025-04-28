package dev.codesoapbox.backity.core.filemanagement.infrastructure.config;

import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import dev.codesoapbox.backity.core.filemanagement.domain.FilePathProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedFileManagementBeanConfig {

    @Bean
    FilePathProvider filePathProvider(@Value("${backity.default-path-template}") String defaultPathTemplate,
                                      FileManager fileManager) {
        return new FilePathProvider(defaultPathTemplate, fileManager);
    }
}
