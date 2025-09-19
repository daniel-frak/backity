package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.core.backup.infrastructure.config.FileBackupWebSocketBeanConfig;
import dev.codesoapbox.backity.core.discovery.infrastructure.config.GameContentDiscoveryWebSocketBeanConfig;
import dev.codesoapbox.backity.core.logs.infrastructure.config.LogsWebSocketBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.WebSocketConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.WebSocketEventPublisherBeanConfig;
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
        WebSocketEventPublisherBeanConfig.class,

        // Project - specific
        LogsWebSocketBeanConfig.class,
        GameContentDiscoveryWebSocketBeanConfig.class,
        FileBackupWebSocketBeanConfig.class
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebSocketEventHandlerTestBeans {
}
