package dev.codesoapbox.backity.testing.time.config;

import dev.codesoapbox.backity.testing.time.FakeClock;
import jakarta.validation.ClockProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.LocalDateTime;

public class FakeTimeBeanConfig {

    // Year 2000 chosen (instead of Epoch) to allow timestamps in the past
    public static final LocalDateTime DEFAULT_NOW = LocalDateTime.of(
            2000, 1, 1, 0, 0);
    public static final FakeClock CLOCK = FakeClock.at(DEFAULT_NOW);

    @Bean
    FakeClock fixedClock() {
        return CLOCK;
    }

    /**
     * Freezes time for bean validation, so that annotations like {@code @FutureOrPresent} don't break in the future.
     */
    @Bean
    ValidationConfigurationCustomizer validationConfigurationCustomizer(Clock clock) {
        FakeClockProvider fixedClockProvider = new FakeClockProvider(clock);
        return c -> c.clockProvider(fixedClockProvider);
    }

    @RequiredArgsConstructor
    private static class FakeClockProvider implements ClockProvider {

        private final Clock clock;

        @Override
        public Clock getClock() {
            return clock;
        }
    }
}
