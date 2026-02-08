package dev.codesoapbox.backity.core.sourcefile.infrastructure.config;

import dev.codesoapbox.backity.core.sourcefile.domain.SourceFileRepository;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileJpaEntityMapper;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileJpaEntitySpringRepository;
import dev.codesoapbox.backity.core.sourcefile.infrastructure.adapters.driven.persistence.jpa.SourceFileJpaRepository;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.JpaRepositoryBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@JpaRepositoryBeanConfiguration
public class SourceFileJpaRepositoryBeanConfig {

    @Bean
    SourceFileJpaEntityMapper sourceFileJpaEntityMapper() {
        return Mappers.getMapper(SourceFileJpaEntityMapper.class);
    }

    @Bean
    SourceFileRepository sourceFileRepository(SourceFileJpaEntitySpringRepository springRepository,
                                              SourceFileJpaEntityMapper entityMapper) {
        return new SourceFileJpaRepository(springRepository, entityMapper);
    }
}
