package dev.codesoapbox.backity.core.discovery.domain.exceptions;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class InvalidGameContentDiscoveryProgressPercentageException extends DomainInvariantViolationException {

    public InvalidGameContentDiscoveryProgressPercentageException(int percentage) {
        super("Percentage must be between 0 and 100, got: " + percentage);
    }
}
