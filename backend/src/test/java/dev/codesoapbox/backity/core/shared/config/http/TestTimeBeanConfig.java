package dev.codesoapbox.backity.core.shared.config.http;

import dev.codesoapbox.backity.testing.FakeClock;
import jakarta.validation.ClockProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.validation.ValidationConfigurationCustomizer;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.LocalDateTime;

public class TestTimeBeanConfig {

    public static final LocalDateTime FIXED_DATE_TIME = LocalDateTime.of(
            1970, 1, 1, 0, 0);
    public static final FakeClock CLOCK = FakeClock.at(FIXED_DATE_TIME);

    @Bean
    public Clock fixedClock() {
        return CLOCK;
    }

    /**
     * Freezes time for bean validation, so that annotations like {@code @FutureOrPresent} don't break in the future.
     */
    @Bean
    public ValidationConfigurationCustomizer validationConfigurationCustomizer(Clock clock) {
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
