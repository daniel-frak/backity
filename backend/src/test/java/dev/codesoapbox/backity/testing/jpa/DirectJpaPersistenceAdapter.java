package dev.codesoapbox.backity.testing.jpa;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;

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
            ObjectProvider<DirectJpaPersistenceStrategy<?, ?>> customStrategies,
            ApplicationContext context
    ) {
        this.entityManager = entityManager;
        this.strategiesByDomainClass = getStrategies(customStrategies, context);
    }

    private Map<Class<?>, DirectJpaPersistenceStrategy<?, ?>> getStrategies(
            ObjectProvider<DirectJpaPersistenceStrategy<?, ?>> customStrategies, ApplicationContext context
    ) {
        Map<Class<?>, DirectJpaPersistenceStrategy<?, ?>> strategies = createDefaultStrategies(context);

        customStrategies.forEach(customStrategy ->
                strategies.put(customStrategy.getDomainObjectClass(), customStrategy));

        return strategies;
    }

    private Map<Class<?>, DirectJpaPersistenceStrategy<?, ?>> createDefaultStrategies(ApplicationContext context) {
        Map<Class<?>, DirectJpaPersistenceStrategy<?, ?>> strategies = new HashMap<>();

        for (MapperBasedDirectJpaPersistenceStrategy strategy : createMapperBasedStrategies(context)) {
            DirectJpaPersistenceStrategy<?, ?> duplicate = strategies.put(strategy.getDomainObjectClass(), strategy);
            if (duplicate != null) {
                throw new IllegalStateException(
                        "Multiple mappers found for %s: %s and %s".formatted(
                                strategy.getDomainObjectClass().getName(), duplicate, strategy
                        )
                );
            }
        }
        return strategies;
    }

    private List<MapperBasedDirectJpaPersistenceStrategy> createMapperBasedStrategies(
            ApplicationContext context
    ) {
        return Arrays.stream(context.getBeanDefinitionNames())
                .flatMap(beanName -> createMapperBasedStrategiesFromBean(context, beanName).stream())
                .toList();
    }

    private List<MapperBasedDirectJpaPersistenceStrategy> createMapperBasedStrategiesFromBean(
            ApplicationContext context,
            String beanName
    ) {
        Class<?> beanType = context.getType(beanName, false);
        if (beanType == null
                || DirectJpaPersistenceStrategy.class.isAssignableFrom(beanType)) {
            return emptyList();
        }

        return MapperBasedDirectJpaPersistenceStrategy.createFrom(beanType, () -> context.getBean(beanName));
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

        for (T domainObject : domainObjects) {
            entityManager.persist(strategy.toEntity(domainObject));
        }

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

        return (DirectJpaPersistenceStrategy<T, E>) strategy;
    }

    public <T> T getPersistedDomainObject(T domainObject) {
        // Verify persisted state rather than the managed entity's in-memory state:
        entityManager.flush();
        entityManager.clear();

        DirectJpaPersistenceStrategy<T, Object> strategy = getStrategyFor(domainObject);
        Object entity = strategy.findPersistedEntity(entityManager, domainObject);

        return entity == null
                ? null
                : strategy.toDomain(entity);
    }

    public boolean exists(Object domainObject) {
        DirectJpaPersistenceStrategy<Object, Object> strategy = getStrategyFor(domainObject);

        entityManager.flush();
        entityManager.clear();

        return strategy.findPersistedEntity(entityManager, domainObject) != null;
    }
}
