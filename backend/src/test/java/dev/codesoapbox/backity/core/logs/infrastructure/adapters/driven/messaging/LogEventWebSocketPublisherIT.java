package dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.LogEventWebSocketPublisher;
import dev.codesoapbox.backity.core.logs.infrastructure.adapters.driven.messaging.ws.LogWebSocketTopics;
import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedEvent;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebSocketEventHandlerTest
class LogEventWebSocketPublisherIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private LogEventWebSocketPublisher eventPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
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