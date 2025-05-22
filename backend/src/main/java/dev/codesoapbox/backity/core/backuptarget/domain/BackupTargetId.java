package dev.codesoapbox.backity.core.backuptarget.domain;

import java.util.UUID;

public record BackupTargetId(
        UUID value
) {

    public BackupTargetId(String value) {
        this(UUID.fromString(value));
    }

    public static BackupTargetId newInstance() {
        return new BackupTargetId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
