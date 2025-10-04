package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.shared.infrastructure.config.WebSocketBrokerConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.WebSocketEventPublisherBeanConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketMessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.annotation.*;

/**
 * Creates a minimal Spring Boot context which allows sending WebSocket messages.
 * Note: The context here could probably be slimmed down.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {
        WebSocketBrokerConfig.class,
        WebSocketEventPublisherBeanConfig.class
})
@ImportAutoConfiguration({
        WebSocketMessagingAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        WebSocketServletAutoConfiguration.class,
        EmbeddedWebServerFactoryCustomizerAutoConfiguration.class,
        ServletWebServerFactoryAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class,
        WebMvcAutoConfiguration.class
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebSocketPublisherTest {
}
