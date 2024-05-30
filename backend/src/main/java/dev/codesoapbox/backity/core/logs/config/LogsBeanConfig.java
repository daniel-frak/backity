package dev.codesoapbox.backity.core.logs.config;

import dev.codesoapbox.backity.core.logs.adapters.driven.logback.LogbackLogService;
import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.LogEventWebSocketPublisher;
import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model.LogCreatedWsEventMapper;
import dev.codesoapbox.backity.core.logs.domain.services.LogEventPublisher;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class LogsBeanConfig {

    @Bean
    LogEventPublisher logEventPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        LogCreatedWsEventMapper mapper = Mappers.getMapper(LogCreatedWsEventMapper.class);
        return new LogEventWebSocketPublisher(mapper, simpMessagingTemplate);
    }

    @Bean
    LogService logService(LogEventPublisher logEventPublisher, @Value("${in-memory-logs.max}") Integer maxLogs) {
        return new LogbackLogService(logEventPublisher, maxLogs);
    }
}
