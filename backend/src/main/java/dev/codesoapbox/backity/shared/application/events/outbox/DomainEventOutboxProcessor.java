package dev.codesoapbox.backity.shared.application.events.outbox;

import dev.codesoapbox.backity.shared.domain.Page;
import dev.codesoapbox.backity.shared.domain.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.locks.ReentrantLock;

// @TODO Move to different package
// @TODO Test
@RequiredArgsConstructor
public class DomainEventOutboxProcessor {

    private static final int EVENT_BATCH_SIZE = 10;
    private final DomainEventOutboxRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    /*
     * Note that this won't work if multiple instances of the application are running.
     * If that is ever needed, a Distributed Lock should be used (e.g., Shedlock).
     */
    private final ReentrantLock lock = new ReentrantLock();

    public void processOutbox() {
        if (!lock.tryLock()) {
            return;
        }
        try {
            // @TODO Process in while loop until no more events
            Page<OutboxEvent> events = repository.findAllUnprocessedOrderedByCreatedAtAsc(
                    new Pagination(0, EVENT_BATCH_SIZE));
            for (OutboxEvent outboxEvent : events.content()) {
                eventPublisher.publishEvent(outboxEvent.domainEvent());
                repository.delete(outboxEvent);
            }
        } finally {
            lock.unlock();
        }
    }
}
