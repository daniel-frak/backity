package dev.codesoapbox.backity.shared.infrastructure.config.slices;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

/// Configuration for event listeners using Spring's [org.springframework.context.event.EventListener]
/// and [org.springframework.transaction.event.TransactionalEventListener].
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration(proxyBeanMethods = false)
public @interface SpringEventListenerSliceConfiguration {
}
