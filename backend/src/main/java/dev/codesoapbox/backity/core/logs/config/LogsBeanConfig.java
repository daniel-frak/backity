package dev.codesoapbox.backity.core.logs.config;

import dev.codesoapbox.backity.core.logs.adapters.driven.logback.LogbackLogService;
import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.LogSpringMessageService;
import dev.codesoapbox.backity.core.logs.adapters.driven.messaging.model.LogCreatedWsMessageMapper;
import dev.codesoapbox.backity.core.logs.domain.services.LogMessageService;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Configuration
public class LogsBeanConfig {

    @Bean
    LogMessageService logMessageService(SimpMessagingTemplate simpMessagingTemplate) {
        LogCreatedWsMessageMapper mapper = Mappers.getMapper(LogCreatedWsMessageMapper.class);
        return new LogSpringMessageService(mapper, simpMessagingTemplate);
    }

    @Bean
    LogService logsService(LogMessageService messageService, @Value("${in-memory-logs.max}") Integer maxLogs) {
        return new LogbackLogService(messageService, maxLogs);
    }
}
