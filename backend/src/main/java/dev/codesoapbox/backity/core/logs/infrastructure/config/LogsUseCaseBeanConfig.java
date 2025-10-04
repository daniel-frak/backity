package dev.codesoapbox.backity.core.logs.infrastructure.config;

import dev.codesoapbox.backity.core.logs.application.GetLogsUseCase;
import dev.codesoapbox.backity.core.logs.domain.services.LogService;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.UseCaseBeanConfiguration;
import org.springframework.context.annotation.Bean;

@UseCaseBeanConfiguration
public class LogsUseCaseBeanConfig {

    @Bean
    GetLogsUseCase getLogsUseCase(LogService logService) {
        return new GetLogsUseCase(logService);
    }
}
