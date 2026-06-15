package dev.codesoapbox.backity.core.backuptarget.infrastructure.config;

import dev.codesoapbox.backity.core.backuptarget.domain.BackupTargetRepository;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa.BackupTargetJpaEntityMapper;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa.BackupTargetJpaRepository;
import dev.codesoapbox.backity.core.backuptarget.infrastructure.adapters.driven.persistence.jpa.BackupTargetSpringRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.JpaRepositorySliceConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@JpaRepositorySliceConfiguration
public class BackupTargetJpaRepositoryBeanConfig {

    @Bean
    BackupTargetJpaEntityMapper backupTargetJpaEntityMapper() {
        return Mappers.getMapper(BackupTargetJpaEntityMapper.class);
    }

    @Bean
    BackupTargetRepository backupTargetRepository(BackupTargetSpringRepository springRepository,
                                                  BackupTargetJpaEntityMapper entityMapper) {
        return new BackupTargetJpaRepository(springRepository, entityMapper);
    }
}
