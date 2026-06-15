package dev.codesoapbox.backity.shared.infrastructure.config.jpa;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageableMapper;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.JpaRepositorySliceConfiguration;
import org.springframework.context.annotation.Bean;

@JpaRepositorySliceConfiguration
public class SharedJpaRepositoryBeanConfig {

    @Bean
    SpringPageMapper springPageMapper() {
        return new SpringPageMapper();
    }

    @Bean
    SpringPageableMapper springPageableMapper() {
        return new SpringPageableMapper();
    }
}
