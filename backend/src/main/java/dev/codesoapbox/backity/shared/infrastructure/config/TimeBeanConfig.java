package dev.codesoapbox.backity.shared.infrastructure.config;

import dev.codesoapbox.backity.shared.infrastructure.config.slices.SystemServiceBeanConfiguration;
import org.springframework.boot.validation.autoconfigure.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@SystemServiceBeanConfiguration
public class TimeBeanConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    /// Provides time for bean validation
    @Bean
    ValidationConfigurationCustomizer validationConfigurationCustomizer(Clock clock) {
        return c -> c.clockProvider(() -> clock);
    }

    /// Provides time for entity auditing ([CreatedDate], [LastModifiedDate])
    @Bean
    DateTimeProvider auditingDateTimeProvider(Clock clock) {
        return () -> Optional.of(LocalDateTime.now(clock));
    }
}
