package dev.codesoapbox.backity.core.logs.infrastructure.config;

import dev.codesoapbox.backity.core.logs.domain.services.LogEventPublisher;
import dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.LogEventWebSocketPublisher;
import dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.model.LogCreatedWsEventMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws.WebSocketEventPublisher;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.WebSocketEventForwarderBeanConfiguration;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;

@WebSocketEventForwarderBeanConfiguration
public class WebSocketLogEventPublisherBeanConfig {

    @Bean
    LogEventPublisher logEventPublisher(WebSocketEventPublisher webSocketEventPublisher) {
        LogCreatedWsEventMapper mapper = Mappers.getMapper(LogCreatedWsEventMapper.class);
        return new LogEventWebSocketPublisher(mapper, webSocketEventPublisher);
    }
}
