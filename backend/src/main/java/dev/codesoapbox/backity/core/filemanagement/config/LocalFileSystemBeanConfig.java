package dev.codesoapbox.backity.core.filemanagement.config;

import dev.codesoapbox.backity.core.filemanagement.adapters.driven.LocalFileSystem;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalFileSystemBeanConfig {

    @Bean
    FileManager fileManager() {
        return new LocalFileSystem();
    }
}
