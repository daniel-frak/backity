package dev.codesoapbox.backity.core.filecopy.domain;

import lombok.NonNull;

import java.util.UUID;

public record FileCopyId(
        @NonNull UUID value
) {

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
}
