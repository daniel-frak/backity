package dev.codesoapbox.backity.gameproviders.gog.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driven.api.spring.auth.GogAuthSpringService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.schedule.GogAuthRefreshSpringScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GogAuthSchedulerBeanConfig {

    @Bean
    public GogAuthRefreshSpringScheduler gogAuthSpringScheduler(GogAuthSpringService gogAuthSpringService) {
        return new GogAuthRefreshSpringScheduler(gogAuthSpringService);
    }
}
