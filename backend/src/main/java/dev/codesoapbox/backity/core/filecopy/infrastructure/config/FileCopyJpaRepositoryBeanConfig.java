package dev.codesoapbox.backity.core.filecopy.infrastructure.config;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.FileCopyJpaEntityMapper;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.FileCopyJpaRepository;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.FileCopySpringRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PageEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PaginationEntityMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileCopyJpaRepositoryBeanConfig {

    @Bean
    FileCopyJpaEntityMapper fileCopyJpaEntityMapper() {
        return Mappers.getMapper(FileCopyJpaEntityMapper.class);
    }

    @Bean
    FileCopyRepository fileCopyRepository(FileCopySpringRepository springRepository,
                                          FileCopyJpaEntityMapper entityMapper,
                                          PageEntityMapper pageEntityMapper,
                                          PaginationEntityMapper paginationEntityMapper,
                                          DomainEventPublisher domainEventPublisher) {
        return new FileCopyJpaRepository(
                springRepository, entityMapper, pageEntityMapper, paginationEntityMapper, domainEventPublisher);
    }
}
