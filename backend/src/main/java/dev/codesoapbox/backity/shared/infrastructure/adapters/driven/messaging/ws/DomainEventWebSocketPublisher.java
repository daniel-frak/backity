package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws;

import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Slf4j
@RequiredArgsConstructor
public class DomainEventWebSocketPublisher implements DomainEventPublisher {

    @SuppressWarnings("java:S6411") // Cannot implement Comparable for Class type
    private final Map<Class<? extends DomainEvent>, DomainEventHandler<? extends DomainEvent>> handlers =
            new HashMap<>();

    public void addHandlers(List<DomainEventHandler<?>> domainEventHandlers) {
        handlers.putAll(domainEventHandlers.stream()
                .collect(toMap(DomainEventHandler::getEventClass, e -> e)));
    }

    @Override
    public <T extends DomainEvent> void publish(T event) {
        @SuppressWarnings("unchecked")
        DomainEventHandler<T> handler = (DomainEventHandler<T>) handlers.get(event.getClass());
        if (handler != null) {
            handler.handle(event);
        } else {
            throw new IllegalArgumentException("No handler found for event type: " + event.getClass().getName());
        }
    }
}