package dev.codesoapbox.backity.core.logs.config;

import dev.codesoapbox.backity.core.logs.application.GetLogsUseCase;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogsUseCaseBeanConfig {

    @Bean
    public GetLogsUseCase getLogsUseCase(LogService logService) {
        return new GetLogsUseCase(logService);
    }
}
