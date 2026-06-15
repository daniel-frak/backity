package dev.codesoapbox.backity.testing.jpa;

import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/// Allows interacting with the database directly through simple JPA queries
/// so that repository tests can be made completely independent of each other.
public class DirectJpaPersistenceAdapter {

    private final TestEntityManager entityManager;

    private final Map<Class<?>, DirectJpaPersistenceStrategy<?, ?>> strategiesByDomainClass;

    public DirectJpaPersistenceAdapter(
            TestEntityManager entityManager,
            List<DirectJpaPersistenceStrategy<?, ?>> strategies
    ) {
        this.entityManager = entityManager;

        this.strategiesByDomainClass = strategies.stream()
                .collect(Collectors.toMap(
                        DirectJpaPersistenceStrategy::getDomainObjectClass,
                        Function.identity()
                ));
    }

    @SafeVarargs
    public final <T> void persist(T... domainObjects) {
        if (domainObjects == null || domainObjects.length == 0) {
            throw new IllegalArgumentException("At least one domain object is required");
        }

        DirectJpaPersistenceStrategy<T, ?> strategy = getStrategyFor(domainObjects[0]);

        for (T domainObject : domainObjects) {
            entityManager.persist(strategy.toEntity(domainObject));
        }

        entityManager.flush();
    }

    @SuppressWarnings("unchecked")
    private <T, E> DirectJpaPersistenceStrategy<T, E> getStrategyFor(T domainObject) {
        if (domainObject == null) {
            throw new IllegalArgumentException("domainObject must not be null");
        }

        DirectJpaPersistenceStrategy<?, ?> strategy = strategiesByDomainClass.get(domainObject.getClass());

        if (strategy == null) {
            throw new IllegalStateException(
                    "No %s registered for %s".formatted(
                            DirectJpaPersistenceStrategy.class.getSimpleName(),
                            domainObject.getClass().getName()
                    )
            );
        }

        return (DirectJpaPersistenceStrategy<T, E>)
                strategiesByDomainClass.get(domainObject.getClass());
    }

    public <T> void persist(List<T> domainObjects) {
        if (domainObjects == null || domainObjects.isEmpty()) {
            throw new IllegalArgumentException("At least one domain object is required");
        }

        DirectJpaPersistenceStrategy<T, ?> strategy = getStrategyFor(domainObjects.getFirst());

        domainObjects.forEach(
                domainObject -> entityManager.persist(strategy.toEntity(domainObject))
        );

        entityManager.flush();
    }

    public <T> T getPersistedDomainObject(T domainObject) {
        DirectJpaPersistenceStrategy<T, ?> strategy = getStrategyFor(domainObject);

        Object entity = strategy.findPersistedEntity(entityManager, domainObject);

        return entity == null
                ? null
                : strategy.toDomain(cast(entity));
    }

    @SuppressWarnings("unchecked")
    private <E> E cast(Object entity) {
        return (E) entity;
    }

    public <T> boolean exists(T domainObject) {
        DirectJpaPersistenceStrategy<T, ?> strategy =
                getStrategyFor(domainObject);

        return strategy.findPersistedEntity(entityManager, domainObject) != null;
    }
}
