package dev.codesoapbox.backity.core.logs.config;

import dev.codesoapbox.backity.core.logs.adapters.driven.logback.LogbackLogsService;
import dev.codesoapbox.backity.core.logs.domain.services.LogsService;
import dev.codesoapbox.backity.core.shared.domain.services.MessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogsBeanConfig {

    @Bean
    LogsService logsService(MessageService messageService, @Value("${in-memory-logs.max}") Integer maxLogs) {
        return new LogbackLogsService(messageService, maxLogs);
    }
}
