package dev.codesoapbox.backity.core.gamefile.domain.exceptions;

import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class GameFileNotBackedUpException extends DomainInvariantViolationException {

    public GameFileNotBackedUpException(GameFileId gameFileId) {
        super("GameFile (id=" + gameFileId + ") is not backed up");
    }
}
