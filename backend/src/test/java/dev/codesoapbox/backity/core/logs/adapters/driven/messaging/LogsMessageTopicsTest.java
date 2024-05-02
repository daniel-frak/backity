package dev.codesoapbox.backity.core.logs.adapters.driven.messaging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LogsMessageTopicsTest {

    @Test
    void toStringShouldReturnValue() {
        String result = LogsMessageTopics.LOGS.toString();

        assertThat(result).isEqualTo("/topic/logs");
    }
}