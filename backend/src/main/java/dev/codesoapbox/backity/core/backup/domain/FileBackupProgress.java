package dev.codesoapbox.backity.core.backup.domain;

public record FileBackupProgress(
        int percentage,
        long timeLeftSeconds
) {
}
