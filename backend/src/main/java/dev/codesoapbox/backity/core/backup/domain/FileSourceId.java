package dev.codesoapbox.backity.core.backup.domain;

import lombok.NonNull;

import java.util.Objects;

public record FileSourceId(@NonNull String value) implements Comparable<FileSourceId> {

    @Override
    public int compareTo(FileSourceId fileSourceId) {
        return Objects.compare(value, fileSourceId.value(), String::compareTo);
    }
}
