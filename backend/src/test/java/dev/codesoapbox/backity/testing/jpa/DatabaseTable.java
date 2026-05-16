package dev.codesoapbox.backity.testing.jpa;

import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/// Simplifies persistence operations for JPA repository tests
public class DatabaseTable<T, E> {

    private final TestEntityManager entityManager;
    private final Function<T, E> toEntity;
    private final Function<E, T> toDomain;
    private final BiFunction<TestEntityManager, T, E> persistedEntityFinder;

    public DatabaseTable(TestEntityManager entityManager,
                         Function<T, E> toEntity,
                         Function<E, T> toDomain,
                         BiFunction<TestEntityManager, T, E> persistedEntityFinder) {
        this.entityManager = entityManager;
        this.toEntity = toEntity;
        this.toDomain = toDomain;
        this.persistedEntityFinder = persistedEntityFinder;
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
