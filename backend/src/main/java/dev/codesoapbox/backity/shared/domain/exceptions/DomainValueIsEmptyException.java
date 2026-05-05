package dev.codesoapbox.backity.shared.domain.exceptions;

public class DomainValueIsEmptyException extends DomainInvariantViolationException {

    public DomainValueIsEmptyException(String property) {
        super("%s is empty".formatted(property));
    }
}
