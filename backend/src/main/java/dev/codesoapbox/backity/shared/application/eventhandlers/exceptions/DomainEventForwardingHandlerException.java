package dev.codesoapbox.backity.shared.application.eventhandlers.exceptions;

public class DomainEventForwardingHandlerException extends RuntimeException {

    public DomainEventForwardingHandlerException(Class<?> eventClass) {
        super("One or more forwarders failed for event class: " + eventClass.getSimpleName());
    }
}
