package dev.codesoapbox.backity.shared.domain;

public interface DomainEventPublisher {

    <T extends DomainEvent> void publish(T event);
}
