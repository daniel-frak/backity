package dev.codesoapbox.backity.core.filecopy.domain;

import lombok.NonNull;

import java.util.UUID;

public record FileCopyId(
        @NonNull UUID value
) implements Comparable<FileCopyId> {

    public FileCopyId(String value) {
        this(UUID.fromString(value));
    }

    public static FileCopyId newInstance() {
        return new FileCopyId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(FileCopyId other) {
        return this.value.compareTo(other.value);
    }
}
