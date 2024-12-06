package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.logs.domain.model.LogCreatedEvent;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

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

        String receivedMessage = messageChannel.receiveMessage(LogWebSocketTopics.LOGS.wsDestination());
        String expectedJson = """
                {
                    "message": "someMessage",
                    "maxLogs" : 123
                }
                """;
        assertReceivedMessageIs(receivedMessage, expectedJson);
    }

    private void assertReceivedMessageIs(String receivedMessage, String expectedJson) throws JsonProcessingException {
        assertThat(receivedMessage).isNotNull();
        assertThat(objectMapper.readTree(receivedMessage))
                .isEqualTo(objectMapper.readTree(expectedJson));
    }
}