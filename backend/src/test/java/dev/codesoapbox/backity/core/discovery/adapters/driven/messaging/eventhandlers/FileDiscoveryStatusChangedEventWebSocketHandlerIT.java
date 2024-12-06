package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.FileDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestFileDiscoveryEvents;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@WebSocketEventHandlerTest
class FileDiscoveryStatusChangedEventWebSocketHandlerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private FileDiscoveryStatusChangedEventWebSocketHandler eventHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        FileDiscoveryStatusChangedEvent event = TestFileDiscoveryEvents.statusChanged();

        eventHandler.handle(event);

        String receivedMessage = messageChannel.receiveMessage(
                FileDiscoveryWebSocketTopics.FILE_DISCOVERY_STATUS_CHANGED.wsDestination());
        String expectedJson = """
                {
                    "gameProviderId":"TestGameProviderId",
                    "isInProgress": true
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