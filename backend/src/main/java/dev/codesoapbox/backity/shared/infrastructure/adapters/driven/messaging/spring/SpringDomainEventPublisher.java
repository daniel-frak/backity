package dev.codesoapbox.backity.shared.infrastructure.adapters.driven.messaging.spring;

import dev.codesoapbox.backity.shared.domain.DomainEvent;
import dev.codesoapbox.backity.shared.domain.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;

@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public <T extends DomainEvent> void publish(T event) {
        eventPublisher.publishEvent(event);
    }
}