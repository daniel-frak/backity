package dev.codesoapbox.backity.core.files.adapters.driving.api.http.model;

import dev.codesoapbox.backity.core.files.domain.game.GameId;

public class GameIdJsonMapper {

    public String toJson(GameId id) {
        if (id == null) {
            return null;
        }
        return id.value().toString();
    }
}
