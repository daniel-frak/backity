package dev.codesoapbox.backity.shared.application.eventhandlers;

import java.lang.annotation.*;

/**
 * Forwards a domain event to an external service, mapping it to an Integration Event.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DomainEventForwarder {
}
