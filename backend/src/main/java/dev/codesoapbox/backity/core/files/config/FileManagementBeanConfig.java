package dev.codesoapbox.backity.core.files.config;

import dev.codesoapbox.backity.core.files.adapters.driven.files.RealFileManager;
import dev.codesoapbox.backity.core.files.domain.backup.services.FileManager;
import dev.codesoapbox.backity.core.files.domain.backup.services.FilePathProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileManagementBeanConfig {

    @Bean
    FileManager fileManager() {
        return new RealFileManager();
    }

    @Bean
    FilePathProvider filePathProvider(@Value("${default-path-template}") String defaultPathTemplate,
                                      FileManager fileManager) {
        return new FilePathProvider(defaultPathTemplate, fileManager);
    }
}
