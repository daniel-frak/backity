package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.core.backup.infrastructure.config.FileBackupWebSocketBeanConfig;
import dev.codesoapbox.backity.core.discovery.infrastructure.config.GameContentDiscoveryWebSocketBeanConfig;
import dev.codesoapbox.backity.core.gamefile.domain.GameFileRepository;
import dev.codesoapbox.backity.core.logs.infrastructure.config.LogsWebSocketBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.DomainEventPublisherBeanConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.WebSocketConfig;
import dev.codesoapbox.backity.testing.messaging.config.SharedWebSocketEventHandlerTestConfig;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

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
        GameContentDiscoveryWebSocketBeanConfig.class,
        FileBackupWebSocketBeanConfig.class
})
@MockitoBean(types = {
        GameFileRepository.class
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebSocketEventHandlerTestBeans {
}
