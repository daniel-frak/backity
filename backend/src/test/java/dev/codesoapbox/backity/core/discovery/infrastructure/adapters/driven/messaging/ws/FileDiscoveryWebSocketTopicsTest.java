package dev.codesoapbox.backity.core.discovery.infrastructure.adapters.driven.messaging.ws;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FileDiscoveryWebSocketTopicsTest {

    @Test
    void wsDestinationShouldReturnValue() {
        String result = FileDiscoveryWebSocketTopics.FILE_DISCOVERED.wsDestination();

        assertThat(result).isEqualTo("/topic/file-discovery/file-discovered");
    }

    @Test
    void toStringShouldReturnWsDestination() {
        assertThat(FileDiscoveryWebSocketTopics.FILE_DISCOVERED)
                .hasToString("/topic/file-discovery/file-discovered");
    }
}