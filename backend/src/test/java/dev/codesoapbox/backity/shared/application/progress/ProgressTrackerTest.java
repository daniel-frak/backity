package dev.codesoapbox.backity.shared.application.progress;

import dev.codesoapbox.backity.testing.time.FakeClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Named.named;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ProgressTrackerTest {

    private FakeClock clock;

    @BeforeEach
    void setUp() {
        // Don't start at EPOCH to better test timeLeft():
        clock = new FakeClock(Clock.fixed(Instant.EPOCH.plus(1, ChronoUnit.DAYS), ZoneId.of("UTC")));
    }

    @Nested
    class Reset {

        @Test
        void shouldInitialize() {
            var tracker = new ProgressTracker(clock);
            tracker.reset(20);
            tracker.increment();

            assertThat(tracker.getProgressInfo().percentage()).isEqualTo(5);
        }

        @Test
        void shouldReset() {
            var tracker = new ProgressTracker(10L, clock);
            tracker.increment();

            tracker.reset(20);
            tracker.increment();

            assertThat(tracker.getProgressInfo().percentage()).isEqualTo(5);
        }
    }

    @Nested
    class Increment {

        @Test
        void shouldIncrementAndGetCorrectPercentage() {
            var tracker = new ProgressTracker(10L, clock);

            tracker.increment();

            assertThat(tracker.getProgressInfo().percentage()).isEqualTo(10);
        }

        @Test
        void shouldIncrementByAndGetCorrectPercentage() {
            var tracker = new ProgressTracker(10L, clock);

            tracker.incrementBy(2L);

            assertThat(tracker.getProgressInfo().percentage()).isEqualTo(20);
        }

        @Test
        void shouldNotIncrementByPastTotalElements() {
            var tracker = new ProgressTracker(10L, clock);

            tracker.incrementBy(20L);

            assertThat(tracker.getProgressInfo().percentage()).isEqualTo(100);
        }

        @Test
        void shouldNotIncrementPastTotalElements() {
            var tracker = new ProgressTracker(10L, clock);

            for (int i = 0; i < 20; i++) {
                tracker.increment();
            }
            assertThat(tracker.getProgressInfo().percentage()).isEqualTo(100);
        }
    }

    @Nested
    class GetProgressInfo {

        static Stream<Arguments> progressArguments() {
            return Stream.of(
                    arguments(named("halfway done", 5), 10, 50, 10L),
                    arguments(named("mostly done", 8), 100, 80, 25L),
                    arguments(named("complete", 10), 10, 100, 0L)
            );
        }

        @ParameterizedTest(name = "when {0}")
        @MethodSource("progressArguments")
        void getProgressInfoShouldShowCorrectProgress(Integer trackerIncrement, Integer timeOffset,
                                                      Integer expectedPercentage, Long expectedSecondsLeft) {
            var tracker = new ProgressTracker(10L, clock);
            clock.moveForward(Duration.of(timeOffset, ChronoUnit.SECONDS));
            tracker.incrementBy(trackerIncrement);
            ProgressInfo progressInfo = tracker.getProgressInfo();

            assertThat(progressInfo.percentage()).isEqualTo(expectedPercentage);
            assertThat(progressInfo.timeLeft()).isEqualTo(Duration.of(expectedSecondsLeft, ChronoUnit.SECONDS));
        }

        @Test
        void getProgressInfoShouldShowNoProgressWhenNeverIncremented() {
            var tracker = new ProgressTracker(10L, clock);
            ProgressInfo progressInfo = tracker.getProgressInfo();

            assertThat(progressInfo.percentage()).isZero();
            assertThat(progressInfo.timeLeft()).isNull();
        }
    }
}