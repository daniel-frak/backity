package dev.codesoapbox.backity.shared.infrastructure.config.openapi;

import io.swagger.v3.core.converter.ModelConverter;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

@Configuration
public class OpenApiGenericsFixConfig {

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public ModelConverter genericsFixingModelConverter(ObjectMapperProvider omp) {
        return new GenericsFixingModelConverter(omp);
    }
}
