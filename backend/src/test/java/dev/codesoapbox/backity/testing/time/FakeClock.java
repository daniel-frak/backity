package dev.codesoapbox.backity.testing.time;

import lombok.AllArgsConstructor;

import java.time.*;

@AllArgsConstructor
public class FakeClock extends Clock {

    private Clock currentClock;

    public static FakeClock at(LocalDate localDate) {
        ZoneId zoneId = ZoneId.of("UTC");
        return new FakeClock(Clock.fixed(localDate.atStartOfDay(zoneId).toInstant(), zoneId));
    }

    public static FakeClock at(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.of("UTC");
        return new FakeClock(Clock.fixed(localDateTime.toInstant(ZoneOffset.UTC), zoneId));
    }

    public static FakeClock atEpochUtc() {
        return new FakeClock(Clock.fixed(Instant.EPOCH, ZoneId.of("UTC")));
    }

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
