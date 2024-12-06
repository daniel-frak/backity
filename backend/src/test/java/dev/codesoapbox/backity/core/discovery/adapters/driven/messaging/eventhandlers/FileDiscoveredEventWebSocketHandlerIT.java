package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.FileDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveredEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestFileDiscoveryEvents;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@WebSocketEventHandlerTest
class FileDiscoveredEventWebSocketHandlerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private FileDiscoveredEventWebSocketHandler eventHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishWebSocketEvent() throws IOException {
        FileDiscoveredEvent event = TestFileDiscoveryEvents.discovered();

        eventHandler.handle(event);

        String receivedMessage = messageChannel.receiveMessage(
                FileDiscoveryWebSocketTopics.FILE_DISCOVERED.wsDestination());
        String expectedJson = """
                {
                  "originalGameTitle": "Original game title",
                  "originalFileName": "originalFileName",
                  "fileTitle": "fileTitle",
                  "size": "5 KB"
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