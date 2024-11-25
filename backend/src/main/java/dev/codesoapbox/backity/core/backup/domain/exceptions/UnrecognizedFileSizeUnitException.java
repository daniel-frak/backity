package dev.codesoapbox.backity.core.backup.domain.exceptions;

import dev.codesoapbox.backity.core.shared.domain.exceptions.DomainInvariantViolationException;

public class UnrecognizedFileSizeUnitException extends DomainInvariantViolationException {

    public UnrecognizedFileSizeUnitException(String unit) {
        super("File size unit unrecognized: " + unit);
    }
}
