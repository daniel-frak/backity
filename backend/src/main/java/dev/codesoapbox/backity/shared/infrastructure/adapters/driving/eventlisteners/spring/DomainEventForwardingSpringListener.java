package dev.codesoapbox.backity.shared.infrastructure.adapters.driving.eventlisteners.spring;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwardingHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;

@RequiredArgsConstructor
public class DomainEventForwardingSpringListener {

    private final DomainEventForwardingHandler eventHandler;

    @EventListener
    public void handle(Object event) {
        if (!event.getClass().getPackageName().startsWith(BackityApplication.class.getPackageName())) {
            return;
        }
        eventHandler.handle(event);
    }
}