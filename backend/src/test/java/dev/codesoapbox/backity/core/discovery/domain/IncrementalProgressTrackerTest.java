package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.testing.FakeClock;
import org.junit.jupiter.api.BeforeEach;
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

class IncrementalProgressTrackerTest {

    private IncrementalProgressTracker tracker;
    private FakeClock clock;

    static Stream<Arguments> progressArguments() {
        return Stream.of(
                arguments(named("halfway done", 5), 10, 50, 10L),
                arguments(named("mostly done", 8), 100, 80, 25L),
                arguments(named("complete", 10), 10, 100, 0L)
        );
    }

    @BeforeEach
    void setUp() {
        // Don't start at EPOCH to better test timeLeft():
        clock = new FakeClock(Clock.fixed(Instant.EPOCH.plus(1, ChronoUnit.DAYS), ZoneId.of("UTC")));
        tracker = new IncrementalProgressTracker(10L, clock);
    }

    @Test
    void shouldIncrementAndGetCorrectPercentage() {
        tracker.increment();
        assertThat(tracker.getProgressInfo().percentage()).isEqualTo(10);
    }

    @Test
    void shouldIncrementByAndGetCorrectPercentage() {
        tracker.incrementBy(2L);
        assertThat(tracker.getProgressInfo().percentage()).isEqualTo(20);
    }

    @Test
    void shouldNotIncrementByPastTotalElements() {
        tracker.incrementBy(20L);
        assertThat(tracker.getProgressInfo().percentage()).isEqualTo(100);
    }

    @Test
    void shouldNotIncrementPastTotalElements() {
        for (int i = 0; i < 20; i++) {
            tracker.increment();
        }
        assertThat(tracker.getProgressInfo().percentage()).isEqualTo(100);
    }

    @ParameterizedTest(name = "when {0}")
    @MethodSource("progressArguments")
    void getProgressInfoShouldShowCorrectProgress(Integer trackerIncrement, Integer timeOffset,
                                                  Integer expectedPercentage, Long expectedSecondsLeft) {
        clock.moveForward(Duration.of(timeOffset, ChronoUnit.SECONDS));
        tracker.incrementBy(trackerIncrement);
        ProgressInfo progressInfo = tracker.getProgressInfo();

        assertThat(progressInfo.percentage()).isEqualTo(expectedPercentage);
        assertThat(progressInfo.timeLeft()).isEqualTo(Duration.of(expectedSecondsLeft, ChronoUnit.SECONDS));
    }

    @Test
    void getProgressInfoShouldShowNoProgressWhenNeverIncremented() {
        ProgressInfo progressInfo = tracker.getProgressInfo();

        assertThat(progressInfo.percentage()).isZero();
        assertThat(progressInfo.timeLeft()).isNull();
    }
}