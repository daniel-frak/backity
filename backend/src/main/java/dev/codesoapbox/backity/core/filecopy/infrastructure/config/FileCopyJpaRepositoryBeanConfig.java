package dev.codesoapbox.backity.core.filecopy.infrastructure.config;

import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.FileCopyJpaRepository;
import dev.codesoapbox.backity.core.filecopy.infrastructure.adapters.driven.persistence.jpa.FileCopySpringRepository;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyRepository;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.PageEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.PaginationEntityMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileCopyJpaRepositoryBeanConfig {

    @Bean
    FileCopyRepository fileCopyRepository(FileCopySpringRepository springRepository,
                                          PageEntityMapper pageEntityMapper,
                                          PaginationEntityMapper paginationEntityMapper,
                                          DomainEventPublisher domainEventPublisher) {
        return new FileCopyJpaRepository(
                springRepository, pageEntityMapper, paginationEntityMapper, domainEventPublisher);
    }
}
