package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.eventhandlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.codesoapbox.backity.core.discovery.domain.events.GameContentDiscoveryProgressChangedEvent;
import dev.codesoapbox.backity.core.discovery.domain.events.TestGameContentDiscoveryEvent;
import dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws.GameContentDiscoveryWebSocketTopics;
import dev.codesoapbox.backity.testing.messaging.TestMessageChannel;
import dev.codesoapbox.backity.testing.messaging.annotations.WebSocketEventHandlerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

@WebSocketEventHandlerTest
class GameContentDiscoveryProgressChangedEventWebSocketHandlerIT {

    @Autowired
    private TestMessageChannel messageChannel;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private GameContentDiscoveryProgressChangedEventWebSocketHandler eventHandler;

    @Test
    void shouldPublishWebSocketEvent() throws JsonProcessingException {
        GameContentDiscoveryProgressChangedEvent event = TestGameContentDiscoveryEvent.progressChanged();

        applicationEventPublisher.publishEvent(event);

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