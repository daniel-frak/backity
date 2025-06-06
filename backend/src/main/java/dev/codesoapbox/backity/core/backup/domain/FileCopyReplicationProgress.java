package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.backup.domain.exceptions.InvalidReplicationProgressPercentageException;
import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;

import java.time.Duration;

public record FileCopyReplicationProgress(
        FileCopyId fileCopyId,
        int percentage,
        Duration timeLeft
) {

    private static final int PERCENTAGE_MAX = 100;

    public FileCopyReplicationProgress {
        if (percentage < 0 || percentage > PERCENTAGE_MAX) {
            throw new InvalidReplicationProgressPercentageException(percentage);
        }
    }
}
