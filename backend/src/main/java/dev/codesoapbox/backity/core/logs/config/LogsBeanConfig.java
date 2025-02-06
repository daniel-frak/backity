package dev.codesoapbox.backity.core.logs.config;

import dev.codesoapbox.backity.core.logs.adapters.driven.logback.LogbackLogService;
import dev.codesoapbox.backity.core.logs.domain.services.LogEventPublisher;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogsBeanConfig {

    @Bean
    LogService logService(LogEventPublisher logEventPublisher,
                          @Value("${backity.in-memory-logs.max}") Integer maxLogs) {
        return new LogbackLogService(logEventPublisher, maxLogs);
    }
}
