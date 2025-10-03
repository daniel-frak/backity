package dev.codesoapbox.backity.shared.infrastructure.config.events.exceptions;

public class DomainEventForwarderException extends RuntimeException {

    public DomainEventForwarderException(Throwable e) {
        super("An exception occurred during Domain Event forwarding", e);
    }
}
