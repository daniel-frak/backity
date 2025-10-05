package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.persistence.jpa.outbox;

import dev.codesoapbox.backity.shared.application.events.outbox.DomainEventOutboxProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

// @TODO Move to different package
// @TODO Test
@RequiredArgsConstructor
public class OutboxProcessingSpringScheduler {

    private final DomainEventOutboxProcessor domainEventOutboxProcessor;

    // @TODO Make less frequent, use Hibernate after-commit "CDC" to trigger OutboxProcessor
    @Scheduled(fixedRate = 1000)
    public void triggerOutboxProcessing() {
        domainEventOutboxProcessor.processOutbox();
    }
}
