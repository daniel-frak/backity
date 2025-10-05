package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.shared.domain.DomainEvent;

import java.util.Map;

// @TODO Consider including in listener tests to ensure ser/des bean exists and works correctly for all events?
//       Then no need to write individual ser/des tests, maybe?
// @TODO Use MapStruct instead of manual mapping??
public interface DomainEventOutboxJpaSerializer<T extends DomainEvent> {

    Map<String, Object> serialize(T event);

    T deserialize(Map<String, Object> eventData);

    Class<T> getSupportedEventClass();
}
