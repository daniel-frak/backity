package dev.codesoapbox.backity.core.shared.config.openapi;

import io.swagger.v3.core.converter.ModelConverters;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiGenericsFixConfig {

    public OpenApiGenericsFixConfig(ObjectMapperProvider springDocObjectMapper) {
        GenericsFixingModelConverter converter = new GenericsFixingModelConverter(springDocObjectMapper);
        ModelConverters.getInstance().addConverter(converter);
    }
}
