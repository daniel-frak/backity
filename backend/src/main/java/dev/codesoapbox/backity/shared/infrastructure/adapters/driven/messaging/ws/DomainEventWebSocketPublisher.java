package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.ws;

import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventHandler;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DomainEventWebSocketPublisher implements DomainEventPublisher {

    @SuppressWarnings("java:S6411") // Cannot implement Comparable for Class type
    private final Map<Class<? extends DomainEvent>, List<DomainEventHandler<? extends DomainEvent>>> handlers =
            new HashMap<>();

    public void addHandlers(List<DomainEventHandler<?>> domainEventHandlers) {
        for (DomainEventHandler<?> handler : domainEventHandlers) {
            handlers.computeIfAbsent(handler.getEventClass(), k -> new ArrayList<>())
                    .add(handler);
        }
    }

    @Override
    public <T extends DomainEvent> void publish(T event) {
        @SuppressWarnings("unchecked")
        List<DomainEventHandler<T>> handlersForEvent =
                (List<DomainEventHandler<T>>) (List<?>) handlers.get(event.getClass());

        if (handlersForEvent != null) {
            for (DomainEventHandler<T> handler : handlersForEvent) {
                handler.handle(event);
            }
        } else {
            throw new IllegalArgumentException("No handler found for event type: " + event.getClass().getName());
        }
    }
}