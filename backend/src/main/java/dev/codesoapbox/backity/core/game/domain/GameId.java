package dev.codesoapbox.backity.core.game.domain;

import java.util.UUID;

public record GameId(
        UUID value
) {
    public static GameId newInstance() {
        return new GameId(UUID.randomUUID());
    }
}
