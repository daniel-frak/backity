package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogWebSocketTopicsTest {

    @Test
    void toStringShouldReturnValue() {
        String result = LogWebSocketTopics.LOGS.toString();

        assertThat(result).isEqualTo("/topic/logs");
    }
}