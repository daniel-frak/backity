package dev.codesoapbox.backity.core.sourcefile.domain.exceptions;

import dev.codesoapbox.backity.shared.domain.exceptions.DomainInvariantViolationException;

public class SourceFileUrlEmptyException extends DomainInvariantViolationException {

    public SourceFileUrlEmptyException() {
        super("Source file url is empty");
    }
}
