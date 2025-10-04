package dev.codesoapbox.backity.core.backup.infrastructure.config;

import dev.codesoapbox.backity.core.backup.domain.FileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.core.backup.infrastructure.adapters.driven.persistence.inmemory.InMemoryFileCopyReplicationProgressRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.InMemoryRepositoryBeanConfiguration;
import org.springframework.context.annotation.Bean;

@InMemoryRepositoryBeanConfiguration
public class FileCopyReplicationProgressRepositoryBeanConfig {

    @Bean
    FileCopyReplicationProgressRepository fileCopyReplicationProgressRepository() {
        return new InMemoryFileCopyReplicationProgressRepository();
    }
}
