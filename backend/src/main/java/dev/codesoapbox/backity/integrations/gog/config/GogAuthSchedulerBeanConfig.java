package dev.codesoapbox.backity.integrations.gog.config;

import dev.codesoapbox.backity.integrations.gog.adapters.driven.backups.services.auth.GogAuthSpringService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GogAuthSchedulerBeanConfig {

    @Bean
    public GogAuthSpringScheduler gogAuthSpringScheduler(GogAuthSpringService gogAuthSpringService) {
        return new GogAuthSpringScheduler(gogAuthSpringService);
    }
}
