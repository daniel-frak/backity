package dev.codesoapbox.backity.core.game.infrastructure.adapters.driven.messaging.ws.model.game;

import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.core.game.domain.GameTitle;

public class GameValueObjectWsDtoMapper {

    public String getValue(GameId gameId) {
        return gameId.value().toString();
    }

    public String getValue(GameTitle gameTitle) {
        return gameTitle.value();
    }
}
