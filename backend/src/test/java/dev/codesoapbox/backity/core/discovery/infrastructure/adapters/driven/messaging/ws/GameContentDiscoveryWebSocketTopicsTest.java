package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GameContentDiscoveryWebSocketTopicsTest {

    @Nested
    class WsDestination {

        @Test
        void shouldReturnValue() {
            String result = GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STARTED.wsDestination();

            assertThat(result).isEqualTo("/topic/game-content-discovery/discovery-started");
        }
    }

    @Nested
    class ToString {

        @Test
        void shouldReturnWsDestination() {
            assertThat(GameContentDiscoveryWebSocketTopics.GAME_CONTENT_DISCOVERY_STARTED)
                    .hasToString("/topic/game-content-discovery/discovery-started");
        }
    }
}