package dev.codesoapbox.backity.core.backup.application.exceptions;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class UnrecognizedFileSizeUnitException extends DomainInvariantViolationException {

    public UnrecognizedFileSizeUnitException(String unit) {
        super("File size unit unrecognized: " + unit);
    }
}
