package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebSocketEventHandlerTest
class GameGameContentDiscoveryProgressChangedEventWebSocketHandlerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private GameContentDiscoveryProgressChangedEventWebSocketHandler eventHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        GameContentDiscoveryProgressChangedEvent event = TestGameContentDiscoveryEvent.progressChanged();

        eventHandler.handle(event);

        var expectedJson = """
                {
                    "gameProviderId": "TestGameProviderId",
                    "percentage": 50,
                    "timeLeftSeconds": 999
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_PROGRESS_CHANGED.wsDestination(), expectedJson);
    }
}