package dev.codesoapbox.backity.core.logs.config;

import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.LogEventWebSocketPublisher;
import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model.LogCreatedWsEventMapper;
import dev.codesoapbox.backity.core.logs.domain.services.LogEventPublisher;
import dev.codesoapbox.backity.core.shared.adapters.driven.messaging.WebSocketEventPublisher;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogsWebSocketBeanConfig {

    @Bean
    LogEventPublisher logEventPublisher(WebSocketEventPublisher webSocketEventPublisher) {
        LogCreatedWsEventMapper mapper = Mappers.getMapper(LogCreatedWsEventMapper.class);
        return new LogEventWebSocketPublisher(mapper, webSocketEventPublisher);
    }
}
