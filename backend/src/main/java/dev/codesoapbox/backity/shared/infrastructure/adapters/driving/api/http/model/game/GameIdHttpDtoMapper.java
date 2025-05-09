package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.api.http.model.game;

import dev.codesoapbox.backity.core.game.domain.GameId;

public class GameIdHttpDtoMapper {

    public String toDto(GameId id) {
        if (id == null) {
            return null;
        }
        return id.value().toString();
    }
}
