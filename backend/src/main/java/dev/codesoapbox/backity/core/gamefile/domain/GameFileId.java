package dev.codesoapbox.backity.core.gamefile.domain;

import lombok.NonNull;

import java.util.UUID;

public record GameFileId(
        @NonNull UUID value
) {

    public GameFileId(String value) {
        this(UUID.fromString(value));
    }

    public static GameFileId newInstance() {
        return new GameFileId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
