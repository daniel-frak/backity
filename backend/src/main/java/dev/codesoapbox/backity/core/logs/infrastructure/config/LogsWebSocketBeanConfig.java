package dev.codesoapbox.backity.core.logs.infrastructure.config;

import dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.LogEventWebSocketPublisher;
import dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.model.LogCreatedWsEventMapper;
import dev.codesoapbox.backity.core.logs.domain.services.LogEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.SpringWebSocketEventPublisher;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogsWebSocketBeanConfig {

    @Bean
    LogEventPublisher logEventPublisher(SpringWebSocketEventPublisher springWebSocketEventPublisher) {
        LogCreatedWsEventMapper mapper = Mappers.getMapper(LogCreatedWsEventMapper.class);
        return new LogEventWebSocketPublisher(mapper, springWebSocketEventPublisher);
    }
}
