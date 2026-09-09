package dev.codesoapbox.backity.testing.jpa;

import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/// Allows interacting with the database directly through simple JPA queries
/// so that repository method tests can be made independent of other repository methods.
///
/// Automatically flushes and clears the entity manager
/// so that assertions check persisted state rather than in-memory state from the persistence context.
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
        if (domainObjects == null) {
            throw atLeastOneDomainObjectIsRequiredException();
        }
        persist(Arrays.asList(domainObjects));
    }

    private IllegalArgumentException atLeastOneDomainObjectIsRequiredException() {
        return new IllegalArgumentException("At least one domain object is required");
    }

    public <T> void persist(List<T> domainObjects) {
        if (domainObjects == null || domainObjects.isEmpty()) {
            throw atLeastOneDomainObjectIsRequiredException();
        }

        DirectJpaPersistenceStrategy<T, ?> strategy = getStrategyFor(domainObjects.getFirst());

        domainObjects.forEach(
                domainObject -> entityManager.persist(strategy.toEntity(domainObject))
        );

        // Verify persisted state rather than the managed entity's in-memory state:
        entityManager.flush();
        entityManager.clear();
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

    @SuppressWarnings("unchecked")
    private <E> E cast(Object entity) {
        return (E) entity;
    }

    public <T> T getPersistedDomainObject(T domainObject) {
        // Verify persisted state rather than the managed entity's in-memory state:
        entityManager.flush();
        entityManager.clear();

        DirectJpaPersistenceStrategy<T, ?> strategy = getStrategyFor(domainObject);

        Object entity = strategy.findPersistedEntity(entityManager, domainObject);

        return entity == null
                ? null
                : strategy.toDomain(cast(entity));
    }

    public <T> boolean exists(T domainObject) {
        DirectJpaPersistenceStrategy<T, ?> strategy =
                getStrategyFor(domainObject);

        return strategy.findPersistedEntity(entityManager, domainObject) != null;
    }
}
