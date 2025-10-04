package dev.codesoapbox.backity.core.storagesolution.infrastructure.config.slices;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

/**
 * Configuration for a local file system StorageSolution.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration(proxyBeanMethods = false)
public @interface LocalFileSystemStorageSolutionBeanConfiguration {
}
