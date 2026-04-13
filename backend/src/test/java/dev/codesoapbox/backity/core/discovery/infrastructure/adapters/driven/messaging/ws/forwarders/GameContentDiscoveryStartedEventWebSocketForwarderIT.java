package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.forwarders;

import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryStartedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventForwarderTest;
import dev.codesoapbox.backity.testing.messaging.websockets.TestMessageChannel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@WebSocketEventForwarderTest
class GameContentDiscoveryStartedEventWebSocketForwarderIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private GameContentDiscoveryStartedEventWebSocketForwarder forwarder;

    @Test
    void shouldPublishWebSocketEvent() {
        GameContentDiscoveryStartedEvent event = TestGameContentDiscoveryEvent.discoveryStarted();

        forwarder.forward(event);

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