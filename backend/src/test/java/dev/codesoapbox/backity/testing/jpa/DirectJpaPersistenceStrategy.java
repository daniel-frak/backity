package dev.codesoapbox.backity.testing.jpa;

import lombok.Getter;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/// Allows interacting with specific objects in the database directly through simple JPA queries,
/// so that repository tests can be made completely independent of each other.
///
/// Used via [DirectJpaPersistenceAdapter].
public abstract class DirectJpaPersistenceStrategy<T, E> {

    private final TestEntityManager entityManager;
    private final Function<T, E> toEntity;
    private final Function<E, T> toDomain;
    private final BiFunction<TestEntityManager, T, E> persistedEntityFinder;

    @Getter
    private final Class<T> domainObjectClass;

    public DirectJpaPersistenceStrategy(TestEntityManager entityManager,
                                        Function<T, E> toEntity,
                                        Function<E, T> toDomain,
                                        BiFunction<TestEntityManager, T, E> persistedEntityFinder,
                                        Class<T> domainObjectClass) {
        this.entityManager = entityManager;
        this.toEntity = toEntity;
        this.toDomain = toDomain;
        this.persistedEntityFinder = persistedEntityFinder;
        this.domainObjectClass = domainObjectClass;
    }

    @SafeVarargs
    public final void persist(T... domainObjects) {
        for (T domainObject : domainObjects) {
            entityManager.persist(toEntity.apply(domainObject));
        }
        entityManager.flush();
    }

    public void persist(List<T> domainObjects) {
        domainObjects.forEach(domainObject -> entityManager.persist(toEntity.apply(domainObject)));
        entityManager.flush();
    }

    public T getPersistedDomainObject(T domainObject) {
        E persistedEntity = getPersistedEntity(domainObject);
        return toDomain.apply(persistedEntity);
    }

    public E getPersistedEntity(T domainObject) {
        return persistedEntityFinder.apply(entityManager, domainObject);
    }

    public boolean exists(T domainObject) {
        return getPersistedEntity(domainObject) != null;
    }
}
