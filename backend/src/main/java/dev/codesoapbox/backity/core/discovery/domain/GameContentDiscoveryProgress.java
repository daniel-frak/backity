package dev.codesoapbox.backity.core.discovery.domain;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.core.discovery.domain.exceptions.InvalidGameContentDiscoveryProgressPercentageException;

import java.time.Duration;

public record GameContentDiscoveryProgress(
        GameProviderId gameProviderId,
        int percentage,
        Duration timeLeft
) {

    private static final int PERCENTAGE_MAX = 100;

    public GameContentDiscoveryProgress {
        if (percentage < 0 || percentage > PERCENTAGE_MAX) {
            throw new InvalidGameContentDiscoveryProgressPercentageException(percentage);
        }
    }
}
