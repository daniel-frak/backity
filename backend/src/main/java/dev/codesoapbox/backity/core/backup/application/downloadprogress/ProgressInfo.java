package dev.codesoapbox.backity.core.backup.application.downloadprogress;

import java.time.Duration;

public record ProgressInfo(
        int percentage,
        Duration timeLeft
) {

    public static ProgressInfo none() {
        return new ProgressInfo(0, null);
    }
}
