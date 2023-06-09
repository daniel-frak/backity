package dev.codesoapbox.backity.core.files.domain.game;

import java.util.UUID;

public record GameId(
        UUID value
) {
    public static GameId newInstance() {
        return new GameId(UUID.randomUUID());
    }
}
