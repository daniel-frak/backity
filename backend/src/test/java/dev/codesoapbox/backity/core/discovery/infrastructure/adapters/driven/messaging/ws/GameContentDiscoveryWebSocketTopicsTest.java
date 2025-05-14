package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameContentDiscoveryWebSocketTopicsTest {

    @Test
    void wsDestinationShouldReturnValue() {
        String result = GameContentDiscoveryWebSocketTopics.FILE_DISCOVERED.wsDestination();

        assertThat(result).isEqualTo("/topic/game-content-discovery/file-discovered");
    }

    @Test
    void toStringShouldReturnWsDestination() {
        assertThat(GameContentDiscoveryWebSocketTopics.FILE_DISCOVERED)
                .hasToString("/topic/game-content-discovery/file-discovered");
    }
}