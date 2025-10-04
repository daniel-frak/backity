package dev.codesoapbox.backity.core.logs.infrastructure.config;

import dev.codesoapbox.backity.core.logs.domain.services.LogEventPublisher;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.logback.LogbackLogService;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.InternalApplicationServiceBeanConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@InternalApplicationServiceBeanConfiguration
public class LogsBeanConfig {

    @Bean
    LogService logService(LogEventPublisher logEventPublisher,
                          @Value("${backity.in-memory-logs.max}") Integer maxLogs) {
        return new LogbackLogService(logEventPublisher, maxLogs);
    }
}
