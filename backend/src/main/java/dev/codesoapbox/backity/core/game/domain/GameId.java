package dev.codesoapbox.backity.core.game.domain;

import java.util.UUID;

public record GameId(
        UUID value
) {

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
}
