package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.GameFileDiscoveredEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@WebSocketEventHandlerTest
class GameFileDiscoveredEventWebSocketHandlerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private GameFileDiscoveredEventWebSocketHandler eventHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishWebSocketEvent() throws IOException {
        GameFileDiscoveredEvent event = TestGameContentDiscoveryEvent.fileDiscovered();

        eventHandler.handle(event);

        var expectedJson = """
                {
                  "originalGameTitle": "Original game title",
                  "originalFileName": "originalFileName",
                  "fileTitle": "fileTitle",
                  "size": "5 KB"
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(
                GameContentDiscoveryWebSocketTopics.FILE_DISCOVERED.wsDestination(), expectedJson);
    }
}