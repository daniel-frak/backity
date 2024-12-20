package dev.codesoapbox.backity.testing.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.backup.config.FileBackupWebSocketBeanConfig;
import dev.codesoapbox.backity.core.discovery.config.FileDiscoveryWebSocketBeanConfig;
import dev.codesoapbox.backity.core.logs.config.LogsWebSocketBeanConfig;
import dev.codesoapbox.backity.infrastructure.config.DomainEventPublisherBeanConfig;
import dev.codesoapbox.backity.infrastructure.config.WebSocketConfig;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessagingTemplate;

/**
 * Motivation for the class:
 * <p>
 * While creating an application context for WebSocket event handler tests does not take a long time, it can lead to
 * the context cache filling up and evicting other, more expensive contexts (such as those for testing repositories).
 * <p>
 * Thus, making all WebSocket event handler tests share a single application context should protect against
 * cache eviction slowing down the tests.
 */
@TestConfiguration
@Import({
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
public class SharedWebSocketEventHandlerTestConfig {

    @Bean
    TestMessageChannel testMessageChannel(ObjectMapper objectMapper) {
        return new TestMessageChannel(objectMapper);
    }

    @Bean
    SimpMessagingTemplate simpMessagingTemplate(TestMessageChannel messageChannel) {
        var simpMessagingTemplate = new SimpMessagingTemplate(messageChannel);
        var messageConverter = new MappingJackson2MessageConverter();
        simpMessagingTemplate.setMessageConverter(messageConverter);

        return simpMessagingTemplate;
    }
}
