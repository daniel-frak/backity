package dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.discovery.adapters.driven.messaging.ws.FileDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestFileDiscoveryEvent;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
        FileDiscoveryStatusChangedEvent event = TestFileDiscoveryEvent.statusChanged();

        eventHandler.handle(event);

        var expectedJson = """
                {
                    "gameProviderId":"TestGameProviderId",
                    "isInProgress": true
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(
                FileDiscoveryWebSocketTopics.FILE_DISCOVERY_STATUS_CHANGED.wsDestination(), expectedJson);
    }
}