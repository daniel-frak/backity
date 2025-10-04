package dev.codesoapbox.backity.shared.infrastructure.config.slices;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

/**
 * Configuration for event forwarders using
 * {@link dev.codesoapbox.backity.shared.application.eventhandlers.DomainEventForwarder}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration(proxyBeanMethods = false)
public @interface WebSocketEventForwarderBeanConfiguration {
}
