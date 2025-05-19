package dev.codesoapbox.backity.core.gamefile.domain.exceptions;

import dev.codesoapbox.backity.core.backup.domain.GameProviderId;
import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class FileSourceUrlEmptyException extends DomainInvariantViolationException {

    public FileSourceUrlEmptyException(GameProviderId id) {
        super("Url was empty for File Source with Game Provider id: " + id.value());
    }
}
