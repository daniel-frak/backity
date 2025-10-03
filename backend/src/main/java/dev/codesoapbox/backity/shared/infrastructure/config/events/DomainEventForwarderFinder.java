package dev.codesoapbox.backity.shared.infrastructure.config.events;

import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwarder;
import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import dev.codesoapbox.backity.shared.infrastructure.config.events.exceptions.DomainEventForwarderException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DomainEventForwarderFinder {

    private final ApplicationContext applicationContext;

    /*
     * The use of the generic wildcard type `EventForwardingConsumer<?>` is required here
     * because the actual generic type parameter cannot be captured or inferred at runtime.
     * Consumers are discovered via reflection, and due to type erasure, we cannot retain or
     * verify the concrete type T of `EventForwardingConsumer<T>`. Using a wildcard ensures
     * type safety without making incorrect assumptions about the event type each consumer accepts.
     */
    @SuppressWarnings({
            "java:S1452",
            "java:S6411" // Cannot implement Comparable for Class type
    })
    public Map<Class<?>, List<DomainEventForwardingHandler.EventForwardingConsumer<?>>>
    findEventForwardingConsumersByEventClass() {
        return applicationContext.getBeansWithAnnotation(DomainEventForwarder.class).entrySet().stream()
                .flatMap(entry -> Arrays.stream(entry.getValue().getClass().getDeclaredMethods())
                        .filter(method -> Modifier.isPublic(method.getModifiers()))
                        .filter(method -> method.getParameterCount() == 1)
                        .filter(method -> method.getReturnType().equals(void.class))
                        .map(method -> new AbstractMap.SimpleEntry<>(
                                method.getParameterTypes()[0],
                                toForwarderProvider(entry, method)
                        )))
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())));
    }

    private DomainEventForwardingHandler.EventForwardingConsumer<Object> toForwarderProvider(
            Map.Entry<String, Object> entry, Method method) {
        return domainEvent -> {
            try {
                method.invoke(entry.getValue(), domainEvent);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new DomainEventForwarderException(e);
            }
        };
    }
}
