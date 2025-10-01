package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.forwarders;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStoppedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventForwarderTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebSocketEventForwarderTest
class GameContentDiscoveryStoppedEventWebSocketForwarderIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private GameContentDiscoveryStoppedEventWebSocketForwarder forwarder;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        GameContentDiscoveryStoppedEvent event = TestGameContentDiscoveryEvent.discoveryStopped();

        forwarder.forward(event);

        var expectedJson = """
                {
                    "gameProviderId":"TestGameProviderId",
                    "discoveryResult": {
                      "startedAt": "2022-04-29T15:00:00",
                      "stoppedAt": "2022-04-29T16:00:00",
                      "discoveryOutcome":"SUCCESS",
                      "lastSuccessfulDiscoveryCompletedAt": "2022-04-20T10:00:00",
                      "gamesDiscovered": 5,
                      "gameFilesDiscovered": 70
                    }
                }
                """;
        messageChannel.assertPublishedWebSocketEvent(
                GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STOPPED.wsDestination(),
                expectedJson);
    }
}