package dev.codesoapbox.backity.core.files.discovery.domain.model;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
@ToString
public class IncrementalProgressTracker {

    private final Long totalElements;
    private final long startTime = System.currentTimeMillis();
    private Long currentElement = 1L;

    public void increment() {
        if (totalElements > currentElement) {
            currentElement++;
        }
    }

    public void incrementBy(long value) {
        currentElement += value;
        if (totalElements < currentElement) {
            currentElement = totalElements;
        }
    }

    public ProgressInfo getProgressInfo() {
        if (currentElement <= 1) {
            return ProgressInfo.none();
        }

        double percentage = (currentElement * 100) / (double) totalElements;

        Duration timeLeftDuration = getTimeLeft();

        return ProgressInfo.of((int) percentage, timeLeftDuration);
    }

    private Duration getTimeLeft() {
        long nowTime = System.currentTimeMillis();
        long elapsedTime = nowTime - startTime;
        double timePerElement = (double) elapsedTime / currentElement;
        long timeLeft = (long)(timePerElement * (totalElements - currentElement));
        return Duration.of(timeLeft, ChronoUnit.MILLIS);
    }
}
