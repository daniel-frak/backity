package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.inmemory.InMemoryFileCopyReplicationProgressRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileCopyReplicationProgressRepositoryBeanConfig {

    @Bean
    FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository() {
        return new InMemoryFileCopyReplicationProgressRepository();
    }
}
