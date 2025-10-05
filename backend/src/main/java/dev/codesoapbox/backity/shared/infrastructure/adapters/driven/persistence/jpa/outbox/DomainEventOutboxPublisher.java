package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.shared.application.events.outbox.DomainEventOutboxRepository;
import dev.codesoapbox.backity.shared.application.events.outbox.OutboxEvent;
import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

// @TODO Test
// @TODO Move to different package?
@RequiredArgsConstructor
public class DomainEventOutboxPublisher implements DomainEventPublisher {

    private final DomainEventOutboxRepository repository;

    @Override
    public <T extends DomainEvent> void publish(T event) {
        var outboxEvent = new OutboxEvent(UUID.randomUUID(), event);
        repository.save(outboxEvent);
    }
}
