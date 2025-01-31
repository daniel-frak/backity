package dev.codesoapbox.backity.core.logs.adapters.driven.messaging.ws;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogWebSocketTopicsTest {

    @Test
    void toStringShouldReturnWsDestination() {
        assertThat(LogWebSocketTopics.LOGS)
                .hasToString("/topic/logs");
    }
}