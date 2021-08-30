package dev.codesoapbox.backity.core.files.discovery.domain.model;

import lombok.NonNull;
import lombok.Value;

import java.time.Duration;

@Value(staticConstructor = "of")
public class ProgressInfo {

    @NonNull
    int percentage;

    Duration timeLeft;

    public static ProgressInfo none() {
        return ProgressInfo.of(0, null);
    }
}
