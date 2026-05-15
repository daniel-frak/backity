package dev.codesoapbox.backity.testing.time.config;

import dev.codesoapbox.backity.testing.time.FakeClock;
import org.springframework.boot.validation.autoconfigure.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

public class FakeTimeBeanConfig {

    // Year 2000 chosen (instead of Epoch) to allow timestamps in the past
    public static final LocalDateTime DEFAULT_NOW = LocalDateTime.of(
            2000, 1, 1, 0, 0);
    public static final FakeClock CLOCK = FakeClock.at(DEFAULT_NOW);

    @Bean
    FakeClock fakeClock() {
        return CLOCK;
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
