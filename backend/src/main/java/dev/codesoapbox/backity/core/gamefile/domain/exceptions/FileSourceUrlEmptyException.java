package dev.codesoapbox.backity.core.gamefile.domain.exceptions;

import dev.codesoapbox.backity.core.gamefile.domain.GameFileId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class FileSourceUrlEmptyException extends DomainInvariantViolationException {

    public FileSourceUrlEmptyException(GameFileId id) {
        super("Game file url was null or empty for Game File with id: " + id.value());
    }
}
