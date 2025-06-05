package dev.codesoapbox.backity.core.game.domain;

import java.util.UUID;

public record GameId(
        UUID value
) implements Comparable<GameId> {

    public GameId(String value) {
        this(UUID.fromString(value));
    }

    public static GameId newInstance() {
        return new GameId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public int compareTo(GameId other) {
        return this.value.compareTo(other.value);
    }
}
