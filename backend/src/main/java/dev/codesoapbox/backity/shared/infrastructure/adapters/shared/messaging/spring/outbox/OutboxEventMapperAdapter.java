package dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

@ToString
public final class OutboxEventMapperAdapter<D, O> {

    private final Object mapper;

    private final MethodHandle toOutboxHandle;
    private final MethodHandle toDomainHandle;

    @Getter
    private final Class<O> outboxType;

    @SneakyThrows
    public OutboxEventMapperAdapter(Object mapper,
                                    Method toOutboxMethod,
                                    Method toDomainMethod,
                                    Class<O> outboxType) {
        this.mapper = mapper;
        this.outboxType = outboxType;

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        this.toOutboxHandle = lookup.unreflect(toOutboxMethod).bindTo(mapper);
        this.toDomainHandle = lookup.unreflect(toDomainMethod).bindTo(mapper);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public O toOutbox(D domain) {
        return (O) toOutboxHandle.invoke(domain);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public D toDomain(O outbox) {
        return (D) toDomainHandle.invoke(outbox);
    }
}
