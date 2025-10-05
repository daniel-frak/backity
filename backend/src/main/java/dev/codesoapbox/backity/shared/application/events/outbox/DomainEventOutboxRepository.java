package dev.codesoapbox.backity.shared.application.events.outbox;

import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;

public interface DomainEventOutboxRepository {

    void save(OutboxEvent event);

    Page<OutboxEvent> findAllUnprocessedOrderedByCreatedAtAsc(Pagination pagination);

    void delete(OutboxEvent outboxEvent);
}
