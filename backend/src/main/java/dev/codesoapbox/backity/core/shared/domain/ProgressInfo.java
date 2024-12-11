package dev.codesoapbox.backity.core.shared.domain;

import java.time.Duration;

public record ProgressInfo(
        int percentage,
        Duration timeLeft
) {

    public static ProgressInfo none() {
        return new ProgressInfo(0, null);
    }
}
