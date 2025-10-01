package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.core.logs.infrastructure.config.WebSocketLogEventPublisherBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.WebSocketConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.WebSocketEventPublisherBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.WebSocketEventForwarderBeanConfiguration;
import dev.codesoapbox.backity.testing.messaging.config.SharedSpringWebSocketEventListenerTestConfig;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Import({
        // Test-only
        SharedSpringWebSocketEventListenerTestConfig.class,

        // Spring
        JacksonAutoConfiguration.class,
        WebSocketMessagingAutoConfiguration.class,

        // Project - common
        WebSocketConfig.class,
        WebSocketEventPublisherBeanConfig.class,

        // Project - specific
        WebSocketLogEventPublisherBeanConfig.class
})
@ComponentScan(
        basePackageClasses = BackityApplication.class,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ANNOTATION,
                classes = WebSocketEventForwarderBeanConfiguration.class
        ),
        useDefaultFilters = false
)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebSocketEventPublisherTestBeans {
}
