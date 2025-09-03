package dev.codesoapbox.backity.shared.infrastructure.config.jpa;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageableMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedJpaRepositoryBeanConfig {

    @Bean
    public SpringPageMapper springPageMapper() {
        return new SpringPageMapper();
    }

    @Bean
    public SpringPageableMapper springPageableMapper() {
        return new SpringPageableMapper();
    }
}
