package dev.codesoapbox.backity.core.shared.domain;

public interface DomainEventPublisher {

    <T extends DomainEvent> void publish(T event);
}
