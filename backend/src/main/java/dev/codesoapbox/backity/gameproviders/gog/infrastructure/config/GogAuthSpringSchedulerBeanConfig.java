package dev.codesoapbox.backity.gameproviders.gog.infrastructure.config;

import dev.codesoapbox.backity.gameproviders.gog.domain.GogAuthService;
import dev.codesoapbox.backity.gameproviders.gog.infrastructure.adapters.driving.schedule.GogAuthRefreshSpringScheduler;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.SpringSchedulerBeanConfiguration;
import org.springframework.context.annotation.Bean;

@SpringSchedulerBeanConfiguration
public class GogAuthSpringSchedulerBeanConfig {

    @Bean
    GogAuthRefreshSpringScheduler gogAuthSpringScheduler(GogAuthService gogAuthService) {
        return new GogAuthRefreshSpringScheduler(gogAuthService);
    }
}
