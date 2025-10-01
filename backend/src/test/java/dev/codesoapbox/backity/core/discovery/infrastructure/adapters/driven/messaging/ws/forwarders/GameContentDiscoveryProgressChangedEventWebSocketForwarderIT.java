package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.forwarders;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventForwarderTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebSocketEventForwarderTest
class GameContentDiscoveryProgressChangedEventWebSocketForwarderIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private GameContentDiscoveryProgressChangedEventWebSocketForwarder forwarder;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        GameContentDiscoveryProgressChangedEvent event = TestGameContentDiscoveryEvent.progressChanged();

        forwarder.forward(event);

        var expectedJson = """
                {
                    "gameProviderId": "TestGameProviderId",
                    "percentage": 50,
                    "timeLeftSeconds": 999,
                    "gamesDiscovered": 5,
                    "gameFilesDiscovered": 70
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_PROGRESS_CHANGED.wsDestination(),
                expectedJson);
    }
}