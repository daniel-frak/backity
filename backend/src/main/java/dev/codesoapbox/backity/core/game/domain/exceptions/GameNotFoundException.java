package dev.codesoapbox.backity.core.game.domain.exceptions;

import dev.codesoapbox.backity.core.game.domain.Game;
import dev.codesoapbox.backity.core.game.domain.GameId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class GameNotFoundException extends DomainInvariantViolationException {

    public GameNotFoundException(GameId id) {
        super("Could not find " + Game.class.getSimpleName() + " with id=" + id);
    }
}
