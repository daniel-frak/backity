package dev.codesoapbox.backity.core.shared.domain.exceptions;

public class DomainInvariantViolationException extends RuntimeException {

    public DomainInvariantViolationException(String message) {
        super(message);
    }

    public DomainInvariantViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DomainInvariantViolationException(Throwable cause) {
        super(cause);
    }

    public DomainInvariantViolationException() {
    }
}
