package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class InvalidReplicationProgressPercentageException extends DomainInvariantViolationException {

    public InvalidReplicationProgressPercentageException(int percentage) {
        super("Percentage must be between 0 and 100, got: " + percentage);
    }
}
