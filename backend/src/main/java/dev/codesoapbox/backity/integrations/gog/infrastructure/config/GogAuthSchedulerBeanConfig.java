package dev.codesoapbox.backity.integrations.gog.infrastructure.config;

import dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driven.backups.services.auth.GogAuthSpringService;
import dev.codesoapbox.backity.integrations.gog.infrastructure.adapters.driving.schedule.GogAuthSpringScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GogAuthSchedulerBeanConfig {

    @Bean
    public GogAuthSpringScheduler gogAuthSpringScheduler(GogAuthSpringService gogAuthSpringService) {
        return new GogAuthSpringScheduler(gogAuthSpringService);
    }
}
