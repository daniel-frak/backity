package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.config.slices.SystemServiceBeanConfiguration;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

@SystemServiceBeanConfiguration
public class TimeBeanConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
