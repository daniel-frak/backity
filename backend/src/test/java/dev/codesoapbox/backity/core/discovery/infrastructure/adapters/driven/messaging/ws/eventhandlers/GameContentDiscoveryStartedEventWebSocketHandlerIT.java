package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebSocketEventHandlerTest
class GameContentDiscoveryStartedEventWebSocketHandlerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private GameContentDiscoveryStartedEventWebSocketHandler eventHandler;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        GameContentDiscoveryStartedEvent event = TestGameContentDiscoveryEvent.discoveryStarted();

        eventHandler.handle(event);

        var expectedJson = """
                {
                    "gameProviderId":"TestGameProviderId"
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STARTED.wsDestination(),
                expectedJson);
    }
}