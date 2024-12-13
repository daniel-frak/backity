package dev.codesoapbox.backity.testing.messaging.annotations;

import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.extensions.WebSocketEventHandlerTestExtension;
import dev.codesoapbox.backity.testing.messaging.config.SharedWebSocketEventHandlerTestConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.*;

/**
 * Create a SimpMessageTemplate with a {@link TestMessageChannel}, which can be {@link Autowired}
 * and queried for sent WebSocket messages.
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(WebSocketEventHandlerTestExtension.class)
@Import(SharedWebSocketEventHandlerTestConfig.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface WebSocketEventHandlerTest {
}
