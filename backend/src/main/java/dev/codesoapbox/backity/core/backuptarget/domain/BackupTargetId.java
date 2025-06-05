package dev.codesoapbox.backity.core.backuptarget.domain;

import lombok.NonNull;

import java.util.UUID;

public record BackupTargetId(
        @NonNull UUID value
) implements Comparable<BackupTargetId> {

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

    @Override
    public int compareTo(BackupTargetId other) {
        return this.value.compareTo(other.value);
    }
}
