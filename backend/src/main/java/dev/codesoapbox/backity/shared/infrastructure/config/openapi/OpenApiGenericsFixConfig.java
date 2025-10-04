package dev.codesoapbox.backity.shared.infrastructure.config.openapi;

import dev.codesoapbox.backity.shared.infrastructure.config.slices.OpenApiBeanConfiguration;
import io.swagger.v3.core.converter.ModelConverter;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@OpenApiBeanConfiguration
public class OpenApiGenericsFixConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ModelConverter genericsFixingModelConverter(ObjectMapperProvider omp) {
        return new GenericsFixingModelConverter(omp);
    }
}
