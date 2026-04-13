package dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox.exceptions;

public class OutboxEventMapperNotFoundException extends RuntimeException {

    public OutboxEventMapperNotFoundException(Class<?> domainEventType) {
        super("No OutboxEventMapper found for domain event type: " + domainEventType.getName());
    }
}
