package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.core.backup.config.FileBackupWebSocketBeanConfig;
import dev.codesoapbox.backity.core.discovery.config.FileDiscoveryWebSocketBeanConfig;
import dev.codesoapbox.backity.core.logs.config.LogsWebSocketBeanConfig;
import dev.codesoapbox.backity.infrastructure.config.DomainEventPublisherBeanConfig;
import dev.codesoapbox.backity.infrastructure.config.WebSocketConfig;
import dev.codesoapbox.backity.testing.messaging.config.SharedWebSocketEventHandlerTestConfig;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Import({
        // Test-only
        SharedWebSocketEventHandlerTestConfig.class,

        // Spring
        JacksonAutoConfiguration.class,
        WebSocketMessagingAutoConfiguration.class,

        // Project - common
        WebSocketConfig.class,
        DomainEventPublisherBeanConfig.class,

        // Project - specific
        LogsWebSocketBeanConfig.class,
        FileDiscoveryWebSocketBeanConfig.class,
        FileBackupWebSocketBeanConfig.class
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebSocketEventHandlerTestBeans {
}
