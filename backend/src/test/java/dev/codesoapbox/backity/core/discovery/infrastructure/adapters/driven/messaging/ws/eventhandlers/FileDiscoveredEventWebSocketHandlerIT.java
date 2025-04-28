package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.FileDiscoveredEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestFileDiscoveryEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.FileDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

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
        FileDiscoveredEvent event = TestFileDiscoveryEvent.discovered();

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
                FileDiscoveryWebSocketTopics.FILE_DISCOVERED.wsDestination(), expectedJson);
    }
}