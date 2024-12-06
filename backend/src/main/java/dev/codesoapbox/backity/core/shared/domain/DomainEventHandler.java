package dev.codesoapbox.backity.core.shared.domain;

public interface DomainEventHandler<T extends DomainEvent> {

    Class<T> getEventClass();

    void handle(T event);
}
