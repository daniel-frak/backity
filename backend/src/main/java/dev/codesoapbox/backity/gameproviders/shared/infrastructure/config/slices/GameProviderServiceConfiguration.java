package dev.codesoapbox.backity.gameproviders.shared.infrastructure.config.slices;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

/**
 * Configuration for Game Provider services.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration
public @interface GameProviderServiceConfiguration {
}
