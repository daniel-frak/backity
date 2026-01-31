package dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging;

import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedEvent;
import dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.LogEventWebSocketPublisher;
import dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.LogWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventForwarderTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebSocketEventForwarderTest
class LogEventWebSocketPublisherIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private LogEventWebSocketPublisher eventPublisher;

    @Test
    void shouldPublishWebSocketEvent() {
        LogCreatedEvent event = LogCreatedEvent.of("someMessage", 123);

        eventPublisher.publish(event);

        var expectedJson = """
                {
                    "message": "someMessage",
                    "maxLogs" : 123
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(LogWebSocketTopics.LOGS.wsDestination(), expectedJson);
    }
}