package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.WebSocketEventForwarderBeanConfiguration;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.WebSocketEventPublisherBeanConfiguration;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.config.SharedSpringWebSocketEventListenerTestConfig;
import dev.codesoapbox.backity.testing.messaging.extensions.WebSocketEventPublisherTestExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/**
 * Annotation for a WebSocket event handler.
 * <p>
 * Creates a SimpMessageTemplate with a {@link TestMessageChannel}, which can be {@link Autowired}
 * and queried for sent WebSocket messages.
 *
 * <h1>Motivation for shared context</h1>
 * <p>
 * While creating an application context for WebSocket event handler tests does not take a long time, it can lead to
 * the context cache filling up and evicting other, more expensive contexts (such as those for testing repositories).
 * <p>
 * Thus, making all WebSocket event handler tests share a single application context should protect against
 * cache eviction slowing down the tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@ExtendWith(SpringExtension.class)
@ExtendWith(WebSocketEventPublisherTestExtension.class)
@ContextConfiguration(classes = {
        WebSocketEventForwarderTest.TestContext.class,
        SharedSpringWebSocketEventListenerTestConfig.class,
        JacksonAutoConfiguration.class
})
public @interface WebSocketEventForwarderTest {

    @ComponentScan(
            basePackageClasses = BackityApplication.class,
            includeFilters = @ComponentScan.Filter(
                    type = FilterType.ANNOTATION,
                    classes = {
                            WebSocketEventPublisherBeanConfiguration.class,
                            WebSocketEventForwarderBeanConfiguration.class
                    }
            ),
            useDefaultFilters = false
    )
    @TestConfiguration
    class TestContext {
    }
}
