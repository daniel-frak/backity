package dev.codesoapbox.backity.shared.infrastructure.config.slices;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

/**
 * Configuration for a Spring ApplicationEvent publisher.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface SpringApplicationEventPublisherBeanConfiguration {
}
