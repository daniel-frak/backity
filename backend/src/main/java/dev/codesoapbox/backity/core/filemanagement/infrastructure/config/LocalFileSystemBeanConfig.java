package dev.codesoapbox.backity.core.filemanagement.infrastructure.config;

import dev.codesoapbox.backity.core.filemanagement.infrastructure.adapters.driven.filesystem.LocalFileSystem;
import dev.codesoapbox.backity.core.filemanagement.domain.FileManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(value = "backity.filesystem.s3.enabled", havingValue = "false")
@Configuration
public class LocalFileSystemBeanConfig {

    @Bean
    FileManager localFileSystem() {
        return new LocalFileSystem();
    }
}
