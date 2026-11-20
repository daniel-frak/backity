package dev.codesoapbox.backity.testing.jpa;

import jakarta.persistence.Entity;
import lombok.ToString;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;

/// A [DirectJpaPersistenceStrategy] derived by inspecting an entity mapper's method signatures.
///
/// The mapper must have:
/// - a public single-argument method taking a domain object and returning an entity object.
/// - a public single-argument method taking an entity object and returning a domain object.
///
/// The entity class must be annotated with [Entity].
///
/// # Rationale:
///
/// If an entity mapper is adequately tested via unit tests, then using [DirectJpaPersistenceAdapter] with this strategy
/// is completely safe and greatly simplifies repository tests.
@ConditionalOnExpression("false") // Created manually by DirectJpaPersistenceAdapter
@ToString
final class MapperBasedDirectJpaPersistenceStrategy implements DirectJpaPersistenceStrategy<Object, Object> {

    private final Object mapper;
    private final Method toEntityMethod;
    private final Method toDomainMethod;

    private MapperBasedDirectJpaPersistenceStrategy(Object mapper, Method toEntityMethod, Method toDomainMethod) {
        this.mapper = mapper;
        this.toEntityMethod = toEntityMethod;
        this.toDomainMethod = toDomainMethod;
    }

    static List<MapperBasedDirectJpaPersistenceStrategy> createFrom(
            Class<?> mapperClass, Supplier<?> mapperSupplier
    ) {
        List<Method> methods = getSingleParameterNonStaticMethods(mapperClass);
        List<MapperMethodPair> mapperMethods = getMapperMethodPairs(methods);

        if (mapperMethods.isEmpty()) {
            return emptyList();
        }

        Object suppliedMapper = mapperSupplier.get();

        return mapperMethods.stream()
                .map(mapping -> toStrategy(mapping, suppliedMapper))
                .toList();
    }

    private static List<Method> getSingleParameterNonStaticMethods(Class<?> mapperClass) {
        return Arrays.stream(mapperClass.getMethods())
                .filter(method -> method.getParameterCount() == 1 && !Modifier.isStatic(method.getModifiers()))
                .toList();
    }

    private static List<MapperMethodPair> getMapperMethodPairs(List<Method> methods) {
        return methods.stream()
                .filter(MapperBasedDirectJpaPersistenceStrategy::isToEntityMethod)
                .map(toEntityMethod -> findInverse(toEntityMethod, methods)
                        .map(toDomainMethod -> new MapperMethodPair(toEntityMethod, toDomainMethod)))
                .flatMap(Optional::stream)
                .toList();
    }

    private static boolean isToEntityMethod(Method method) {
        return method.getReturnType().isAnnotationPresent(Entity.class);
    }

    private static Optional<Method> findInverse(Method method, List<Method> candidates) {
        return candidates.stream()
                .filter(candidate -> isInverseOf(candidate, method))
                .reduce((first, second) -> {
                    throw new IllegalStateException(
                            "Ambiguous inverse of %s: both %s and %s match".formatted(method, first, second)
                    );
                });
    }

    private static boolean isInverseOf(Method candidate, Method method) {
        return candidate.getParameterTypes()[0] == method.getReturnType()
                && candidate.getReturnType() == method.getParameterTypes()[0];
    }

    private static MapperBasedDirectJpaPersistenceStrategy toStrategy(MapperMethodPair mapping, Object suppliedMapper) {
        return new MapperBasedDirectJpaPersistenceStrategy(
                suppliedMapper,
                mapping.toEntity(),
                mapping.toDomain()
        );
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<Object> getDomainObjectClass() {
        return (Class<Object>) toEntityMethod.getParameterTypes()[0];
    }

    @Override
    public Object toDomain(Object entity) {
        return ReflectionUtils.invokeMethod(toDomainMethod, mapper, entity);
    }

    @Override
    public Object findPersistedEntity(TestEntityManager entityManager, Object domainObject) {
        Object entity = toEntity(domainObject);
        Object id = Objects.requireNonNull(
                entityManager.getId(entity),
                () -> "Mapped entity has no identifier: " + entity
        );

        return entityManager.find(entity.getClass(), id);
    }

    @Override
    public Object toEntity(Object domainObject) {
        return ReflectionUtils.invokeMethod(toEntityMethod, mapper, domainObject);
    }

    private record MapperMethodPair(Method toEntity, Method toDomain) {
    }
}
