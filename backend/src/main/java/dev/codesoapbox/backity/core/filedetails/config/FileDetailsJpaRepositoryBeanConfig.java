package dev.codesoapbox.backity.core.filedetails.config;

import dev.codesoapbox.backity.core.filedetails.adapters.driven.persistence.jpa.FileDetailsJpaEntityMapper;
import dev.codesoapbox.backity.core.filedetails.adapters.driven.persistence.jpa.FileDetailsJpaEntitySpringRepository;
import dev.codesoapbox.backity.core.filedetails.adapters.driven.persistence.jpa.FileDetailsJpaRepository;
import dev.codesoapbox.backity.core.filedetails.domain.FileDetailsRepository;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PageEntityMapper;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PaginationEntityMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileDetailsJpaRepositoryBeanConfig {

    @Bean
    FileDetailsJpaEntityMapper fileDetailsJpaEntityMapper() {
        return Mappers.getMapper(FileDetailsJpaEntityMapper.class);
    }

    @Bean
    FileDetailsRepository fileDetailsRepository(FileDetailsJpaEntitySpringRepository springRepository,
                                                FileDetailsJpaEntityMapper entityMapper,
                                                PageEntityMapper pageMapper,
                                                PaginationEntityMapper paginationMapper) {
        return new FileDetailsJpaRepository(springRepository, entityMapper, pageMapper, paginationMapper);
    }
}
