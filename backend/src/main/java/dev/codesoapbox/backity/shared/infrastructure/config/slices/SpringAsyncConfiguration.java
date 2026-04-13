package dev.codesoapbox.backity.shared.infrastructure.config.slices;

import org.springframework.context.annotation.Configuration;

import java.lang.annotation.*;

/// Configuration for asynchronous execution
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Configuration(proxyBeanMethods = false)
public @interface SpringAsyncConfiguration {
}
