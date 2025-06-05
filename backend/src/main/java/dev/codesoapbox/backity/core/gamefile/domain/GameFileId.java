package dev.codesoapbox.backity.core.gamefile.domain;

import lombok.NonNull;

import java.util.UUID;

public record GameFileId(
        @NonNull UUID value
) implements Comparable<GameFileId> {

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

    @Override
    public int compareTo(GameFileId other) {
        return this.value.compareTo(other.value);
    }
}
