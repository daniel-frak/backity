package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.BackityApplication;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.WebSocketEventForwarderSliceConfiguration;
import dev.codesoapbox.backity.shared.infrastructure.config.slices.WebSocketEventPublisherSliceConfiguration;
import dev.codesoapbox.backity.testing.messaging.extensions.WebSocketEventPublisherTestExtension;
import dev.codesoapbox.backity.testing.messaging.websockets.SharedSpringWebSocketEventListenerTestConfig;
import dev.codesoapbox.backity.testing.messaging.websockets.TestMessageChannel;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/// Annotation for a WebSocket event handler.
///
/// Creates a SimpMessageTemplate with a [TestMessageChannel], which can be [Autowired]
/// and queried for sent WebSocket messages.
///
/// # Motivation for shared context
///
/// Creating many unique Spring contexts can lead to the context cache filling up and evicting other
/// (potentially expensive) contexts.
///
/// Thus, making all tests of the same slice share a single application context should
/// protect against cache eviction slowing down the tests.
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
                            WebSocketEventPublisherSliceConfiguration.class,
                            WebSocketEventForwarderSliceConfiguration.class
                    }
            ),
            useDefaultFilters = false
    )
    @TestConfiguration
    class TestContext {
    }
}
