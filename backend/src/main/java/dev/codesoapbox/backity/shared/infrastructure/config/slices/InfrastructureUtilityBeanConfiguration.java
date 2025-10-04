package dev.codesoapbox.backity.shared.infrastructure.config.slices;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

/**
 * Configuration for external infrastructural libraries with no business logic.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface InfrastructureUtilityBeanConfiguration {
}
