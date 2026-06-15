package dev.codesoapbox.backity.testing.jpa;

import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

/// Defines how a domain object maps to a JPA entity and how to find it in the database.
///
/// Used via [DirectJpaPersistenceAdapter].
public interface DirectJpaPersistenceStrategy<T, E> {

    Class<T> getDomainObjectClass();

    E toEntity(T domainObject);

    T toDomain(E entity);

    E findPersistedEntity(TestEntityManager entityManager, T domainObject);
}
