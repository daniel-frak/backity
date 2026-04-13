package dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox;

import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Method;
import java.util.*;

public class AnnotationBasedOutboxEventMapperRegistry {

    @SuppressWarnings("java:S6411") // Cannot implement Comparable for Class type
    private final Map<Class<?>, OutboxEventMapperAdapter<?, ?>> mapperAdapters = new HashMap<>();

    public AnnotationBasedOutboxEventMapperRegistry(List<Object> mappers) {
        for (Object mapper : mappers) {
            OutboxEventMapper annotation = getOutboxEventMapperAnnotation(mapper);
            Class<?> domainType = annotation.domain();
            Class<?> outboxType = annotation.outbox();

            OutboxEventMapperAdapter<Object, ?> adapter = createMapperAdapter(mapper, domainType, outboxType);
            validateIsUnique(mapperAdapters, domainType);
            mapperAdapters.put(domainType, adapter);
        }
    }

    private OutboxEventMapperAdapter<Object, ?> createMapperAdapter(
            Object mapper, Class<?> domainType, Class<?> outboxType) {
        Method toOutboxMethod = findMappingMethod(mapper, domainType, outboxType);
        Method toDomainMethod = findMappingMethod(mapper, outboxType, domainType);

        return new OutboxEventMapperAdapter<>(
                mapper,
                toOutboxMethod,
                toDomainMethod,
                outboxType
        );
    }

    private OutboxEventMapper getOutboxEventMapperAnnotation(Object mapper) {
        OutboxEventMapper annotation =
                AnnotatedElementUtils.findMergedAnnotation(mapper.getClass(), OutboxEventMapper.class);

        if (annotation == null) {
            throw new IllegalStateException("No @OutboxEventMapper annotation found on %s"
                    .formatted(mapper.getClass().getName()));
        }

        return annotation;
    }

    @SuppressWarnings("unchecked")
    public <D> Optional<OutboxEventMapperAdapter<D, Object>> getByDomainType(Class<D> domainType) {
        return Optional.ofNullable((OutboxEventMapperAdapter<D, Object>) mapperAdapters.get(domainType));
    }

    @SuppressWarnings("java:S6411") // Cannot implement Comparable for Class type
    private void validateIsUnique(Map<Class<?>, ?> map, Class<?> key) {
        if (map.containsKey(key)) {
            throw new IllegalStateException(
                    "Duplicate OutboxEventMapper for type: " + key.getName()
            );
        }
    }

    private Method findMappingMethod(Object mapper, Class<?> inputType, Class<?> returnType) {
        return Arrays.stream(mapper.getClass().getMethods())
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> m.getParameterTypes()[0].equals(inputType))
                .filter(m -> m.getReturnType().equals(returnType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No method mapping %s -> %s found in %s"
                                .formatted(
                                        inputType.getSimpleName(),
                                        returnType.getSimpleName(),
                                        mapper.getClass().getName()
                                )
                ));
    }
}
