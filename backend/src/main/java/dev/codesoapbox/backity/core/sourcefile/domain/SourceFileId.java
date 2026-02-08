package dev.codesoapbox.backity.core.sourcefile.domain;

import lombok.NonNull;

import java.util.UUID;

public record SourceFileId(
        @NonNull UUID value
) implements Comparable<SourceFileId> {

    public SourceFileId(String value) {
        this(UUID.fromString(value));
    }

    public static SourceFileId newInstance() {
        return new SourceFileId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(SourceFileId other) {
        return this.value.compareTo(other.value);
    }
}
