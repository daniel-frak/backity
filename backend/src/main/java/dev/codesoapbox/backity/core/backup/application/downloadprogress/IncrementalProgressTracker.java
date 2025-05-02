package dev.codesoapbox.backity.core.backup.application.downloadprogress;

import dev.codesoapbox.backity.DoNotMutate;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@ToString
public class IncrementalProgressTracker {

    private static final int MULTIPLIER_FOR_PERCENTAGE = 100;
    private final Long totalElements;
    private final Clock clock;
    private final long startTime;

    private Long processedElementsCount = 0L;

    public IncrementalProgressTracker(Long totalElements, Clock clock) {
        this.totalElements = totalElements;
        this.clock = clock;
        this.startTime = clock.millis();
    }

    public void increment() {
        if (totalElements > processedElementsCount) {
            processedElementsCount++;
        }
    }

    @DoNotMutate // Equivalent mutant (< vs <= both give same result)
    public void incrementBy(long value) {
        processedElementsCount += value;
        if (totalElements < processedElementsCount) {
            processedElementsCount = totalElements;
        }
    }

    public ProgressInfo getProgressInfo() {
        if (processedElementsCount < 1) {
            return ProgressInfo.none();
        }

        double percentage = (processedElementsCount * MULTIPLIER_FOR_PERCENTAGE) / (double) totalElements;

        Duration timeLeftDuration = getTimeLeft();

        return new ProgressInfo((int) percentage, timeLeftDuration);
    }

    private Duration getTimeLeft() {
        long nowTime = clock.millis();
        long elapsedTime = nowTime - startTime;
        double timePerElement = (double) elapsedTime / processedElementsCount;
        long timeLeft = (long) (timePerElement * (totalElements - processedElementsCount));
        return Duration.of(timeLeft, ChronoUnit.MILLIS);
    }
}
