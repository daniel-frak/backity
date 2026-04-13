package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

@RequiredArgsConstructor
public class DomainEventForwardingSpringListener {

    private final DomainEventForwardingHandler eventHandler;

    // Event handling happens completely in-memory, so outbox is not needed
    @Async
    @EventListener
    public void listen(Object event) {
        if (!event.getClass().getPackageName().startsWith(BackityApplication.class.getPackageName())) {
            return;
        }
        eventHandler.handle(event);
    }
}