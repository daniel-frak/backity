package dev.codesoapbox.backity.shared.infrastructure.config.jpa;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PageEntityMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.PaginationEntityMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedJpaRepositoryBeanConfig {

    @Bean
    public PageEntityMapper pageEntityMapper() {
        return new PageEntityMapper();
    }

    @Bean
    public PaginationEntityMapper paginationEntityMapper() {
        return new PaginationEntityMapper();
    }
}
