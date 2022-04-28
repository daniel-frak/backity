package dev.codesoapbox.backity.testing;

import lombok.AllArgsConstructor;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;

@AllArgsConstructor
public class FakeClock extends Clock {

    private Clock currentClock;

    public void moveForward(Duration offsetDuration) {
        currentClock = Clock.offset(currentClock, offsetDuration);
    }

    @Override
    public ZoneId getZone() {
        return currentClock.getZone();
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return currentClock.withZone(zone);
    }

    @Override
    public Instant instant() {
        return currentClock.instant();
    }

    @Override
    public long millis() {
        return currentClock.millis();
    }
}
