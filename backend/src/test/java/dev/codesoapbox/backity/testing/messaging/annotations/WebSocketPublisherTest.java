package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.shared.infrastructure.config.WebSocketBrokerConfig;
import dev.codesoapbox.backity.shared.infrastructure.config.WebSocketEventPublisherBeanConfig;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.tomcat.autoconfigure.servlet.TomcatServletWebServerAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.boot.websocket.autoconfigure.servlet.WebSocketMessagingAutoConfiguration;

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
        TomcatServletWebServerAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class,
        WebMvcAutoConfiguration.class
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebSocketPublisherTest {
}
