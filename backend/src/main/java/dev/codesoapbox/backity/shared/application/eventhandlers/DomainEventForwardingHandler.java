package dev.codesoapbox.backity.shared.application.eventhandlers;

import dev.codesoapbox.backity.DoNotMutate;
import dev.codesoapbox.backity.shared.application.eventhandlers.exceptions.DomainEventForwardingHandlerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
public class DomainEventForwardingHandler {

    @SuppressWarnings("java:S6411") // Cannot implement Comparable for Class type
    private final Map<Class<?>, List<EventForwardingConsumer<?>>> eventForwardingConsumersByClass;

    @DoNotMutate // False positive due to log when forwardingConsumers is NULL or empty
    @SuppressWarnings("unchecked")
    public <T> void handle(T event) {
        Class<?> eventClass = event.getClass();
        List<EventForwardingConsumer<?>> forwardingConsumers = eventForwardingConsumersByClass.get(eventClass);

        if (forwardingConsumers == null || forwardingConsumers.isEmpty()) {
            log.debug("No forwarders found for event class: {}", eventClass.getName());
            return;
        }
        List<Throwable> failures = new ArrayList<>();
        for (EventForwardingConsumer<?> forwardingConsumer : forwardingConsumers) {
            try {
                ((EventForwardingConsumer<T>) forwardingConsumer).accept(event);
            } catch (RuntimeException exception) {
                failures.add(exception);
                log.error("Domain event forwarder [{}] failed for event {}", forwardingConsumer, event, exception);
            }
        }
        if (!failures.isEmpty()) {
            var forwardingException = new DomainEventForwardingHandlerException(eventClass);
            failures.forEach(forwardingException::addSuppressed);
            throw forwardingException;
        }
        log.info("Event successfully handled by {} forwarders: {}", forwardingConsumers.size(), event);
    }

    public interface EventForwardingConsumer<T> extends Consumer<T> {
    }
}
