package dev.codesoapbox.backity.core.shared.config.jpa;

import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PageEntityMapper;
import dev.codesoapbox.backity.core.shared.adapters.driven.persistence.PaginationEntityMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedJpaRepositoryConfig {

    @Bean
    public PageEntityMapper pageEntityMapper() {
        return new PageEntityMapper();
    }

    @Bean
    public PaginationEntityMapper paginationEntityMapper() {
        return new PaginationEntityMapper();
    }
}
