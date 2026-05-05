package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.persistence.jpa;

import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;

import java.util.UUID;

public class GameValueObjectJpaDtoMapper {

    public UUID getValue(GameId gameId) {
        return gameId.value();
    }

    public GameId toGameId(UUID uuid) {
        return new GameId(uuid);
    }

    public String getValue(GameTitle gameTitle) {
        return gameTitle.value();
    }

    public GameTitle toGameTitle(String value) {
        return new GameTitle(value);
    }
}
