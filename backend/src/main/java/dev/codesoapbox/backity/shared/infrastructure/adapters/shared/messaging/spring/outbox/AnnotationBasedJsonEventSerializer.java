package dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox;

import dev.codesoapbox.backity.shared.infrastructure.adapters.shared.messaging.spring.outbox.exceptions.OutboxEventMapperNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.modulith.events.core.EventSerializer;
import tools.jackson.databind.json.JsonMapper;

@RequiredArgsConstructor
public class AnnotationBasedJsonEventSerializer implements EventSerializer {

    private final JsonMapper jsonMapper;
    private final AnnotationBasedOutboxEventMapperRegistry eventMapperRegistry;

    @Override
    public Object serialize(Object domainEvent) {
        @SuppressWarnings("unchecked")
        OutboxEventMapperAdapter<Object, Object> mapperAdapter =
                getMapperAdapter((Class<Object>) domainEvent.getClass());
        Object payload = mapperAdapter.toOutbox(domainEvent);

        return jsonMapper.writeValueAsString(payload);
    }

    @Override
    public <T> T deserialize(Object outboxPayload, Class<T> domainEventType) {
        OutboxEventMapperAdapter<T, Object> eventMapperAdapter =
                getMapperAdapter(domainEventType);

        Object outboxEvent =
                jsonMapper.readerFor(eventMapperAdapter.getOutboxType()).readValue(outboxPayload.toString());
        return eventMapperAdapter.toDomain(outboxEvent);
    }

    private <T> OutboxEventMapperAdapter<T, Object> getMapperAdapter(
            Class<T> domainEventType) {
        return eventMapperRegistry.getByDomainType(domainEventType)
                .orElseThrow(() -> new OutboxEventMapperNotFoundException(domainEventType));
    }
}
