package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStatusChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebSocketEventHandlerTest
class GameGameGameContentDiscoveryStatusChangedEventWebSocketHandlerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private GameContentDiscoveryStatusChangedEventWebSocketHandler eventHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        GameContentDiscoveryStatusChangedEvent event = TestGameContentDiscoveryEvent.statusChanged();

        eventHandler.handle(event);

        var expectedJson = """
                {
                    "gameProviderId":"TestGameProviderId",
                    "isInProgress": true
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STATUS_CHANGED.wsDestination(), expectedJson);
    }
}