package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameContentDiscoveryWebSocketTopicsTest {

    @Test
    void wsDestinationShouldReturnValue() {
        String result = GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STARTED.wsDestination();

        assertThat(result).isEqualTo("/topic/game-content-discovery/discovery-started");
    }

    @Test
    void toStringShouldReturnWsDestination() {
        assertThat(GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STARTED)
                .hasToString("/topic/game-content-discovery/discovery-started");
    }
}