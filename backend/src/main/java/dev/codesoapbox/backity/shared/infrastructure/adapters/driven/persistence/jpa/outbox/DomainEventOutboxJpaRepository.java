package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.shared.application.events.outbox.DomainEventOutboxRepository;
import dev.codesoapbox.backity.shared.application.events.outbox.OutboxEvent;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageMapper;
import dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.SpringPageableMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;

@RequiredArgsConstructor
public class DomainEventOutboxJpaRepository implements DomainEventOutboxRepository {

    private static final Sort SORT_BY_CREATED_AT_ASC = Sort.by(Sort.Direction.ASC, "createdAt");

    private final Clock clock;
    private final DomainEventOutboxSpringRepository springRepository;
    private final SpringPageMapper pageMapper;
    private final SpringPageableMapper paginationMapper;
    private final Map<Class<? extends DomainEvent>, DomainEventOutboxJpaSerializer<?>> eventSerializers;

    @Override
    public void save(OutboxEvent event) {
        DomainEventOutboxJpaSerializer<DomainEvent> eventSerializer = getEventSerializer(event.domainEvent());
        OutboxEventEntity outboxEventEntity = new OutboxEventEntity(
                event.id(),
                event.domainEvent().getClass().getName(), // @TODO This makes it impossible to move events to a different package
                eventSerializer.serialize(event.domainEvent()),
                false,
                LocalDateTime.now(clock)
        );
        springRepository.save(outboxEventEntity);
    }

    @Override
    public Page<OutboxEvent> findAllUnprocessedOrderedByCreatedAtAsc(Pagination pagination) {
        Pageable pageable = paginationMapper.toPageable(pagination, SORT_BY_CREATED_AT_ASC);
        org.springframework.data.domain.Page<OutboxEventEntity> entities =
                springRepository.findAllByProcessedFalse(pageable);
        return pageMapper.toDomain(entities, this::toDomain);
    }

    @Override
    public void delete(OutboxEvent outboxEvent) {
        springRepository.deleteById(outboxEvent.id());
    }

    private OutboxEvent toDomain(OutboxEventEntity outboxEventEntity) {
        DomainEventOutboxJpaSerializer<?> eventSerializer = getEventSerializer(outboxEventEntity);
        return new OutboxEvent(
                outboxEventEntity.getId(),
                eventSerializer.deserialize(outboxEventEntity.getPayload())
        );
    }

    @SneakyThrows
    private DomainEventOutboxJpaSerializer<?> getEventSerializer(OutboxEventEntity outboxEventEntity) {
        Class<?> eventClass = Class.forName(outboxEventEntity.getType());
        return eventSerializers.get(eventClass);
    }

    @SuppressWarnings("unchecked")
    private <T extends DomainEvent> DomainEventOutboxJpaSerializer<T> getEventSerializer(T event) {
        return (DomainEventOutboxJpaSerializer<T>) eventSerializers.get(event.getClass());
    }
}