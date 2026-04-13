package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.spring.CustomEventSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.modulith.events.core.EventSerializer;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class SpringEventSerializationBeanConfig {

    @Primary
    @Bean
    EventSerializer eventSerializer(JsonMapper jsonMapper) {
        return new CustomEventSerializer(() -> jsonMapper);
    }
}
