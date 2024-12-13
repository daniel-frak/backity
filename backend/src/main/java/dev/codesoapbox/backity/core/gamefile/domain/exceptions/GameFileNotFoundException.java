package dev.codesoapbox.backity.core.gamefile.domain.exceptions;

import dev.codesoapbox.backity.core.gamefile.domain.GameFile;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class GameFileNotFoundException extends DomainInvariantViolationException {

    public GameFileNotFoundException(GameFileId id) {
        super("Could not find " + GameFile.class.getSimpleName() + " with id=" + id);
    }
}
