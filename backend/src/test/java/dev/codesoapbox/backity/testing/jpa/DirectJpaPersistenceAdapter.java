package dev.codesoapbox.backity.testing.jpa;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/// Allows interacting with the database directly through simple JPA queries,
/// so that repository tests can be made completely independent of each other.
public class DirectJpaPersistenceAdapter {

    private final Map<Class<?>, DirectJpaPersistenceStrategy<?, ?>> persistenceAdaptersByEntityClass;

    public DirectJpaPersistenceAdapter(
            List<DirectJpaPersistenceStrategy<?, ?>> persistenceAdapters
    ) {
        persistenceAdaptersByEntityClass = persistenceAdapters.stream()
                .collect(Collectors.toMap(DirectJpaPersistenceStrategy::getDomainObjectClass, Function.identity()));
    }

    @SafeVarargs
    public final <T> void persist(T... domainObjects) {
        DirectJpaPersistenceStrategy<T, ?> adapter = getPersistenceAdapterFor(domainObjects[0]);
        adapter.persist(domainObjects);
    }

    @SuppressWarnings("unchecked") // Safe cast
    private <T, E> DirectJpaPersistenceStrategy<T, E> getPersistenceAdapterFor(T domainObject) {
        return (DirectJpaPersistenceStrategy<T, E>) persistenceAdaptersByEntityClass.get(domainObject.getClass());
    }

    public <T> void persist(List<T> domainObjects) {
        DirectJpaPersistenceStrategy<T, ?> adapter = getPersistenceAdapterFor(domainObjects.getFirst());
        adapter.persist(domainObjects);
    }

    public <T, E> T getPersistedDomainObject(T domainObject) {
        DirectJpaPersistenceStrategy<T, E> adapter = getPersistenceAdapterFor(domainObject);
        return adapter.getPersistedDomainObject(domainObject);
    }

    public <T> boolean exists(T domainObject) {
        DirectJpaPersistenceStrategy<T, ?> adapter = getPersistenceAdapterFor(domainObject);
        return adapter.exists(domainObject);
    }
}
