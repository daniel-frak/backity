package dev.codesoapbox.backity.shared.application.progress;

import dev.codesoapbox.backity.DoNotMutate;
import lombok.Getter;
import lombok.ToString;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@ToString
public class ProgressTracker {

    private static final int MULTIPLIER_FOR_PERCENTAGE = 100;

    private final Clock clock;
    private Long totalElements;
    private long startTime;

    @Getter
    private Long processedElementsCount = 0L;

    public ProgressTracker(Clock clock) {
        this.clock = clock;
    }

    public ProgressTracker(long totalElements, Clock clock) {
        this.totalElements = totalElements;
        this.clock = clock;
        this.startTime = clock.millis();
    }

    public void reset(long totalElements) {
        this.totalElements = totalElements;
        this.startTime = clock.millis();
        this.processedElementsCount = 0L;
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
