package dev.codesoapbox.backity.core.backup.domain;

import dev.codesoapbox.backity.core.filecopy.domain.FileCopyId;

import java.time.Duration;

public record FileCopyReplicationProgress(
        FileCopyId fileCopyId,
        int percentage,
        Duration timeLeft
) {
}
