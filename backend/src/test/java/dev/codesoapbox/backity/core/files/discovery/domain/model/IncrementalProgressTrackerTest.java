package dev.codesoapbox.backity.core.files.discovery.domain.model;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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
        clock = new FakeClock(Clock.fixed(Instant.EPOCH, ZoneId.of("UTC")));
        tracker = new IncrementalProgressTracker(10L, clock);
    }

    @Test
    void shouldIncrementAndGetCorrectPercentage() {
        tracker.increment();
        assertEquals(10, tracker.getProgressInfo().percentage());
    }

    @Test
    void shouldIncrementByAndGetCorrectPercentage() {
        tracker.incrementBy(2L);
        assertEquals(20, tracker.getProgressInfo().percentage());
    }

    @Test
    void shouldNotIncrementByPastTotalElements() {
        tracker.incrementBy(20L);
        assertEquals(100, tracker.getProgressInfo().percentage());
    }

    @Test
    void shouldNotIncrementPastTotalElements() {
        for (int i = 0; i < 20; i++) {
            tracker.increment();
        }
        assertEquals(100, tracker.getProgressInfo().percentage());
    }

    @ParameterizedTest(name = "when {0}")
    @MethodSource("progressArguments")
    void getProgressInfoShouldShowCorrectProgress(Integer trackerIncrement, Integer timeOffset,
                                                  Integer expectedPercentage, Long expectedSecondsLeft) {
        clock.moveForward(Duration.of(timeOffset, ChronoUnit.SECONDS));
        tracker.incrementBy(trackerIncrement);
        ProgressInfo progressInfo = tracker.getProgressInfo();

        assertEquals(expectedPercentage, progressInfo.percentage());
        assertEquals(Duration.of(expectedSecondsLeft, ChronoUnit.SECONDS), progressInfo.timeLeft());
    }

    @Test
    void getProgressInfoShouldShowNoProgressWhenNeverIncremented() {
        ProgressInfo progressInfo = tracker.getProgressInfo();

        assertEquals(0, progressInfo.percentage());
        assertNull(progressInfo.timeLeft());
    }
}