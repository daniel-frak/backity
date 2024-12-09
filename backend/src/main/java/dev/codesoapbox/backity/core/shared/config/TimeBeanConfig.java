package dev.codesoapbox.backity.core.shared.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeBeanConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
